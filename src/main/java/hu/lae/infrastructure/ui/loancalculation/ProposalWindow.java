package hu.lae.infrastructure.ui.loancalculation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
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
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.loan.LoanApplicationResult;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.infrastructure.ui.LaeUI;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.component.ComboBox;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
public class ProposalWindow extends Window {
    
    private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);
    
    private final CheckBox stLoanRefinanceCheck = new CheckBox("Refinance");
    private final CheckBox ltLoanRefinanceCheck = new CheckBox("Refinance");
    
    private final LoanCalculator loanCalculator;
    private final Client client;
    
    private final ComboBox<Integer> paybackYearsCombo;
    
    private final ComboBox<FreeCashFlowCalculator> cashflowCalculatorCombo = new ComboBox<>("EBITDA calculation", FreeCashFlowCalculator.calculators());
    
    private final AmountField shortTermLoanField = new AmountField("Short term loan");
    
    private final AmountField longTermLoanField = new AmountField("Long term loan");
    
    private final Label minPaybackYearsLabel = new Label();
    
    private final int maxLoanDuration; 
    
    private final Button submitButton = new Button("Submit for proposal", click -> submit());
    
    public ProposalWindow(LoanCalculator loanCalculator, Client client, LocalDate currentDate) {
        this.loanCalculator = loanCalculator;
        this.client = client;
        setModal(true);
        maxLoanDuration = loanCalculator.riskParameters.maxLoanDurations.maxLoanDuration(client.industry); 

        paybackYearsCombo = new ComboBox<>("Select l/t number of years", generateComboValues(maxLoanDuration));
        paybackYearsCombo.addValueChangeListener(value -> paybackYearsChanged(value.getValue()));
        paybackYearsCombo.setValue(maxLoanDuration);
        
        cashflowCalculatorCombo.setValue(FreeCashFlowCalculator.lastYear);
        cashflowCalculatorCombo.addValueChangeListener(value -> cashFlowCalculationStrategyChanged(value.getValue()));
        
        ltLoanRefinanceCheck.addValueChangeListener(v -> showMinPaybackYears());
        shortTermLoanField.addValueChangeListener(v -> showMinPaybackYears());
        longTermLoanField.addValueChangeListener(v -> showMinPaybackYears());
        
        double yearlyDebtServiceForExistingLoans = client.existingLoans.yealyDebtService(loanCalculator.riskParameters.longTermInterestRate, currentDate);
        setContent(createLayout(yearlyDebtServiceForExistingLoans));
        
        addShortcutListener(VaadinUtil.createErrorSubmissionShortcutListener());
    }
    
    private void showMinPaybackYears() {
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, createLoanRequest(), cashflowCalculatorCombo.getValue(), ltLoanRefinanceCheck.getValue());
        if(minPaybackYears > 0) {
            minPaybackYearsLabel.setValue("Minimum payback duration: " + Formatters.formatYears(minPaybackYears));            
        } else {
            minPaybackYearsLabel.setValue("");
        }
    }

    private Component createLayout(double yearlyDebtServiceForExistingLoans) {
        setResizable(false);
        
        Component existingLoanPanel = createExistingLoansPanel(yearlyDebtServiceForExistingLoans);
        
        Component idealStructurePanel = createIdealStructurePanel();
        
        HorizontalLayout topPanel = new HorizontalLayout(existingLoanPanel, idealStructurePanel);
        
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
        paybackYearsCombo.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        paybackYearsCombo.setWidth("85px");
        paybackYearsCombo.setEmptySelectionAllowed(false);
        
        cashflowCalculatorCombo.setEmptySelectionAllowed(false);
        cashflowCalculatorCombo.setWidth("120px");
        
        shortTermLoanField.setIcon(VaadinIcons.MONEY);
        shortTermLoanField.removeStyleName(ValoTheme.TEXTFIELD_SMALL);
        longTermLoanField.setIcon(VaadinIcons.MONEY);
        longTermLoanField.removeStyleName(ValoTheme.TEXTFIELD_SMALL);
        
        FormLayout layout = new FormLayout(paybackYearsCombo, cashflowCalculatorCombo, shortTermLoanField, longTermLoanField);
        layout.setSizeUndefined();
        layout.setMargin(new MarginInfo(false, false, true, false));
        
        Button helpButton = new Button("Help", click -> UI.getCurrent().addWindow(
                new LoanHelperWindow(loanCalculator, client, paybackYearsCombo.getValue(), cashflowCalculatorCombo.getValue(),
                        new LoanRequest(shortTermLoanField.getAmount(), longTermLoanField.getAmount()),
                        ltLoanRefinanceCheck.getValue(),
                        loanRequest -> {
                            shortTermLoanField.setAmount((long)loanRequest.shortTermLoan);
                            longTermLoanField.setAmount((long)loanRequest.longTermLoan);
                        })));
        helpButton.setIcon(VaadinIcons.SLIDER);
        helpButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        
        VerticalLayout panelLayout = new VerticalLayout(helpButton, layout, minPaybackYearsLabel);
        panelLayout.setComponentAlignment(helpButton, Alignment.TOP_RIGHT);
        panelLayout.setSpacing(false);
        panelLayout.setSizeFull();
        
        return new Panel(panelLayout);
    }
    
    private void paybackYearsChanged(int paybackYears) {
        logger.debug("Payback years combo is set to " + paybackYears);
        //loanSelector1.setPaybackYears(paybackYears);
    }
    
    private void cashFlowCalculationStrategyChanged(FreeCashFlowCalculator calculator) {
        logger.debug("Cash flow calculator strategy changed to " + calculator);
        //loanSelector1.setPaybackYears(paybackYears);
    }
    
    private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }
    
    private Component createExistingLoansPanel(double yearlyDebtServiceForExistingLoans) {
        Label stlabel = new Label("Short term loan");
        stlabel.setWidth("115px");
        AmountField stLoanField = new AmountField(null, "Existing ST Loan");
        stLoanField.setAmount(client.existingLoans.shortTermLoans);
        stLoanField.setWidth("60px");
        stLoanField.setReadOnly(true);
        HorizontalLayout stLayout = new HorizontalLayout(stlabel, stLoanField, stLoanRefinanceCheck);

        Label ltlabel = new Label("Long term loan");
        ltlabel.setWidth("115px");
        AmountField ltLoanField = new AmountField(null, "Existing LT Loan");
        ltLoanField.setAmount(client.existingLoans.longTermLoans);
        ltLoanField.setWidth("60px");
        ltLoanField.setReadOnly(true);
        HorizontalLayout ltLayout = new HorizontalLayout(ltlabel, ltLoanField, ltLoanRefinanceCheck);   
        
        AmountField amountField = new AmountField(null);
        amountField.setAmount((long)yearlyDebtServiceForExistingLoans);
        amountField.setReadOnly(true);
        amountField.setWidth("60px");
        HorizontalLayout debtServiceLayout = new HorizontalLayout(new Label("Yearly Debt Service:"), amountField);
        
        VerticalLayout layout = new VerticalLayout(stLayout, ltLayout, debtServiceLayout);
        layout.setComponentAlignment(debtServiceLayout, Alignment.BOTTOM_CENTER);
        Panel panel = new Panel("Existing loans", layout);
        panel.setSizeUndefined();
        return panel;
    }
    
    private Component createIdealStructurePanel() {
        
        LoanRequest idealLoanRequest = loanCalculator.calculateIdealLoanRequest(client, cashflowCalculatorCombo.getValue());
        
        Label stlabel = new Label("Ideal short term loan");
        stlabel.setWidth("150px");
        AmountField stLoanField = new AmountField(null, "Ideal ST Loan");
        stLoanField.setAmount((long)idealLoanRequest.shortTermLoan);
        stLoanField.setWidth("60px");
        stLoanField.setReadOnly(true);
        HorizontalLayout stLayout = new HorizontalLayout(stlabel, stLoanField);

        Label ltlabel = new Label("Ideal long term loan");
        ltlabel.setWidth("150px");
        AmountField ltLoanField = new AmountField(null, "Ideal LT Loan");
        ltLoanField.setAmount((long)idealLoanRequest.longTermLoan);
        ltLoanField.setWidth("60px");
        ltLoanField.setReadOnly(true);
        HorizontalLayout ltLayout = new HorizontalLayout(ltlabel, ltLoanField);        
        VerticalLayout layout = new VerticalLayout(stLayout, ltLayout);
        Panel panel = new Panel("Ideal structure for " + maxLoanDuration + " years", layout);
        panel.setSizeUndefined();
        
        return panel;
    }
    
    private void submit() {
        List<String> errorMessages = validate();
        if(errorMessages.isEmpty()) {
            super.close();
            UI.getCurrent().addWindow(new DecisionWindow(loanCalculator.riskParameters, client, createLoanRequest()));
        } else {
            Notification.show("Validation error", String.join("\n", errorMessages), Type.ERROR_MESSAGE);
        }
    }
    
    private LoanRequest createLoanRequest() {
        return new LoanRequest(shortTermLoanField.getAmount(), longTermLoanField.getAmount());
    }
    
    private List<String> validate() {
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYearsCombo.getValue(), 0, cashflowCalculatorCombo.getValue(), ltLoanRefinanceCheck.getValue());
        long maxShortTermLoan = (long)loanApplicationResult.maxShortTermLoan;
        
        List<String> errorMessages = new ArrayList<>();
        long shortTermLoan = shortTermLoanField.getAmount();
        long longTermLoan = longTermLoanField.getAmount();
        if(ltLoanRefinanceCheck.getValue()) {
            if(longTermLoan < client.existingLoans.longTermLoans) {
                errorMessages.add("Long term loan request must be enough to cover the existing long term loans");
            }
        }
        if(shortTermLoan > maxShortTermLoan) {
            errorMessages.add("Short term loan must not exceed " + maxShortTermLoan);
        }
        loanApplicationResult = loanCalculator.calculate(client, paybackYearsCombo.getValue(), shortTermLoan, cashflowCalculatorCombo.getValue(), ltLoanRefinanceCheck.getValue());
        if(longTermLoan > loanApplicationResult.maxLongTermLoan) {
            errorMessages.add("Long term loan must not exceed " + (long)loanApplicationResult.maxLongTermLoan);
        }
        
        return errorMessages;
    }
    
    @Override
    public void close() {
        logger.debug("Proposal window is closed");
        super.close();
    }
    
}
