package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.industry.IndustryData;
import hu.lae.domain.legal.LegalParameters;
import hu.lae.domain.loan.DSCRCalculator;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.loan.LoanPreCalculator;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.loan.LoanRequestValidator;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.validation.LiquidityValidator;
import hu.lae.domain.validation.ValidationResult;
import hu.lae.infrastructure.ui.LaeUI;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.component.ComboBox;
import hu.lae.infrastructure.ui.loancalculation.decision.DecisionWindow;
import hu.lae.util.Clock;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
public class ProposalWindow extends Window {
    
    private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);
    
    private final IdealStructurePanel idealStructurePanel;
    
    private final LoanCalculator loanCalculator;
    private final IndustryData industryData;
    private final Client client;
    
    private final ComboBox<FreeCashFlowCalculator> cashflowCalculatorCombo = new ComboBox<>("EBITDA calculation", FreeCashFlowCalculator.calculators());
    
    private final ExistingLoansRefinancingTable existingLoansRefinancingTable;
    
    private final MaturityField maturityField;
    
    private final AmountField shortTermLoanField = new AmountField("Short term loan");
    
    private final AmountField longTermLoanField = new AmountField("Long term loan");
    
    private final Label minPaybackYearsLabel = new Label();
    
    private final Button helpButton = new Button("Help", click -> openLoanHelperWindow());
    
    private final int maxLoanDuration;
    
    private final RiskParameters riskParameters;
    
    private final LegalParameters legalParameters;
    
    private final Button submitButton = new Button("Submit for proposal", click -> submit());
    
    public ProposalWindow(LegalParameters legalParameters, IndustryData industryData, LoanCalculator loanCalculator, Client client, LocalDate currentDate) {
        this.loanCalculator = loanCalculator;
        this.legalParameters = legalParameters;
        this.industryData = industryData;
        this.client = client;
        setModal(true);
        
        riskParameters = loanCalculator.riskParameters;
        maxLoanDuration = loanCalculator.maxLoanDuration;

        existingLoansRefinancingTable = new ExistingLoansRefinancingTable(client.existingLoans, riskParameters.interestRates);

        maturityField = new MaturityField(maxLoanDuration);
        
        cashflowCalculatorCombo.setValue(FreeCashFlowCalculator.average);
        
        cashflowCalculatorCombo.addValueChangeListener(v -> updateIdealStructurePanel());
        cashflowCalculatorCombo.addValueChangeListener(v -> updateMinPaybackYearsLabel());
        shortTermLoanField.addValueChangeListener(v -> updateMinPaybackYearsLabel());
        shortTermLoanField.addValueChangeListener(v -> checkLiquidityRatio());
        longTermLoanField.addValueChangeListener(v -> updateMinPaybackYearsLabel());
        existingLoansRefinancingTable.addRefinanceChangeListener(() -> updateMinPaybackYearsLabel());

        ExistingLoansRefinancing existingLoansRefinancing = existingLoansRefinancingTable.getValue();
        
        LoanRequest idealLoanRequest = loanCalculator.calculateIdealLoanRequest(client, cashflowCalculatorCombo.getValue());
        idealStructurePanel = new IdealStructurePanel(idealLoanRequest, maxLoanDuration);
        
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.interestRates, currentDate);
        double yearlyDebtServiceForExistingShortTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForShortTermLoans(riskParameters.interestRates, idealLoanRequest);
        
        setContent(createLayout(yearlyDebtServiceForExistingShortTermLoans + yearlyDebtServiceForExistingLongTermLoans));
        
        addShortcutListener(VaadinUtil.createErrorSubmissionShortcutListener());
    }
    
    private void updateIdealStructurePanel() {
        LoanRequest idealLoanRequest = loanCalculator.calculateIdealLoanRequest(client, cashflowCalculatorCombo.getValue());
        idealStructurePanel.update(idealLoanRequest);
    }

    private void openLoanHelperWindow() {
        
        LoanRequest loanRequest = createLoanRequest();
        FreeCashFlowCalculator cashFlowCalculator = cashflowCalculatorCombo.getValue();
        
        LoanHelperWindow loanHelperWindow = new LoanHelperWindow(loanCalculator, client, cashFlowCalculator, loanRequest, existingLoansRefinancingTable.getValue(), 
            selectedLoanRequest -> {
                shortTermLoanField.setAmount((long)selectedLoanRequest.shortTermLoan);
                longTermLoanField.setAmount((long)selectedLoanRequest.longTermLoan);
                checkLiquidityRatio();
                updateMinPaybackYearsLabel();
        });
                
        loanHelperWindow.open();
    }

    private void updateMinPaybackYearsLabel() {
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, createLoanRequest(), cashflowCalculatorCombo.getValue(), existingLoansRefinancingTable.getValue());
        if(minPaybackYears > 0) {
            minPaybackYearsLabel.setValue("Minimum payback duration: " + Formatters.formatYears(minPaybackYears));            
        } else {
            minPaybackYearsLabel.setValue("");
        }
    }
    
    private void checkLiquidityRatio() {
        double nonRefinancableShortTermLoans = existingLoansRefinancingTable.getValue().nonRefinancableShortTermLoans();
    	 LiquidityValidator liquidityRatioValidator = new LiquidityValidator(nonRefinancableShortTermLoans, riskParameters.thresholds.liquidityRatio);
         ValidationResult validationResult = liquidityRatioValidator.validateRatio1(client.financialStatementData(), createLoanRequest());
         if(validationResult.isOk()) {
        	 shortTermLoanField.setComponentError(null);
         } else {
        	 shortTermLoanField.setComponentError(new UserError(validationResult.toString()));
         }
    }

    private Component createLayout(double yearlyDebtServiceForExistingLoans) {
        setResizable(false);
        
        HorizontalLayout topPanel = new HorizontalLayout(existingLoansRefinancingTable, idealStructurePanel);
        
        submitButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        
        VerticalLayout layout = new VerticalLayout(
                topPanel,
                createMiddlePanel(),
                submitButton);
        
        layout.setComponentAlignment(topPanel, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(submitButton, Alignment.BOTTOM_CENTER);
        
        Panel panel = new Panel("Proposal", layout);
        panel.addStyleName("colored");
        return panel;
    }
    
    private Component createMiddlePanel() {
        
        cashflowCalculatorCombo.setEmptySelectionAllowed(false);
        cashflowCalculatorCombo.setWidth("120px");
        
        shortTermLoanField.setIcon(VaadinIcons.MONEY);
        shortTermLoanField.removeStyleName(ValoTheme.TEXTFIELD_SMALL);
        longTermLoanField.setIcon(VaadinIcons.MONEY);
        longTermLoanField.removeStyleName(ValoTheme.TEXTFIELD_SMALL);
        
        FormLayout layout = new FormLayout(maturityField, cashflowCalculatorCombo, shortTermLoanField, longTermLoanField);
        layout.setSizeUndefined();
        layout.setMargin(new MarginInfo(false, false, true, false));
        
        helpButton.setIcon(VaadinIcons.SLIDER);
        helpButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        
        VerticalLayout panelLayout = new VerticalLayout(helpButton, layout, minPaybackYearsLabel);
        panelLayout.setComponentAlignment(helpButton, Alignment.TOP_RIGHT);
        panelLayout.setSpacing(false);
        panelLayout.setSizeFull();
        
        return new Panel(panelLayout);
    }
    
    private void submit() {
        LoanRequest loanRequest = createLoanRequest();
        List<String> errorMessages = validate(loanRequest);
        
        if(errorMessages.isEmpty()) {
            super.close();
            ExistingLoansRefinancing existingLoansRefinancing = existingLoansRefinancingTable.getValue();
            FreeCashFlowCalculator freeCashFlowCalculator = cashflowCalculatorCombo.getValue();
            double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
            double dscr = DSCRCalculator.calculateDSCR(riskParameters.interestRates, freeCashFlow, loanRequest, existingLoansRefinancing, Clock.date());
            LoanRequest idealLoanRequest = loanCalculator.calculateIdealLoanRequest(client, freeCashFlowCalculator);
            UI.getCurrent().addWindow(new DecisionWindow(riskParameters, legalParameters, client, existingLoansRefinancing, loanRequest, dscr, idealLoanRequest.sum(), freeCashFlow, this));
        } else {
            Notification.show("Validation error", String.join("\n", errorMessages), Type.ERROR_MESSAGE);
        }
    }
    
    private LoanRequest createLoanRequest() {
        return new LoanRequest(shortTermLoanField.getAmount(), longTermLoanField.getAmount(), Clock.date(), maturityField.getValue());
    }
    
    private List<String> validate(LoanRequest loanRequest) {
        
    	LoanPreCalculator loanPreCalculator = new LoanPreCalculator(riskParameters, legalParameters, industryData);
        LoanRequestValidator loanRequestValidator = new LoanRequestValidator(loanCalculator, loanPreCalculator, industryData);
        
        return loanRequestValidator.validate(cashflowCalculatorCombo.getValue(), client, existingLoansRefinancingTable.getValue(), loanRequest);
    }
    
    @Override
    public void close() {
        logger.debug("Proposal window is closed");
        super.close();
    }
    
}
