package hu.lae.infrastructure.ui.loancalculation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.Client;
import hu.lae.accounting.FreeCashFlowCalculator;
import hu.lae.infrastructure.ui.LaeUI;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.loan.LoanCalculator;

@SuppressWarnings("serial")
public class ProposalWindow extends Window {
    
    private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);

    private final CheckBox stLoanRefinanceCheck = new CheckBox("Refinance");
    private final CheckBox ltLoanRefinanceCheck = new CheckBox("Refinance");
    
    private final LoanCalculator loanCalculator;
    private final Client client;
    
    private final LoanSelector loanSelector1;
    private final LoanSelector loanSelector2;

    private final ComboBox<Integer> paybackYearsCombo;
    
    private final int maxLoanDuration; 
    
    private final Button submitButton = new Button("Submit for proposal", click -> submit());
    
    public ProposalWindow(LoanCalculator loanCalculator, Client client, LocalDate currentDate) {
        this.loanCalculator = loanCalculator;
        this.client = client;
        setModal(true);
        maxLoanDuration = loanCalculator.riskParameters.maxLoanDurations.maxLoanDuration(client.industry); 

        loanSelector1 = new LoanSelector("Average EBITDA", loanCalculator, client, maxLoanDuration, FreeCashFlowCalculator.average);
        loanSelector2 = new LoanSelector("Last year EBITDA", loanCalculator, client, maxLoanDuration, FreeCashFlowCalculator.lastYear);
        paybackYearsCombo = new ComboBox<>(null, generateComboValues(maxLoanDuration));
        paybackYearsCombo.addValueChangeListener(value -> paybackYearsChanged(value.getValue()));
        paybackYearsCombo.setValue(maxLoanDuration);
        
        double yearlyDebtServiceForExistingLoans = client.existingLoans.yealyDebtService(loanCalculator.riskParameters.longTermInterestRate, currentDate);
        setContent(createLayout(yearlyDebtServiceForExistingLoans));
        //if(client.existingLoans.isToBeRefinanced) {
        //    ltLoanSlider.setMinLoanValue((long)yearlyDebtServiceForExistingLoans);
        //}
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
        Component paybackYearsField = createPaybackYearsField();
        VerticalLayout layout = new VerticalLayout(paybackYearsField,
                new HorizontalLayout(loanSelector1, loanSelector2)) ;
        layout.setComponentAlignment(paybackYearsField, Alignment.MIDDLE_CENTER);
        return new Panel(layout);
    }
    
    private Component createPaybackYearsField() {
        Label label = new Label("Select l/t number of years");
        label.addStyleName(ValoTheme.LABEL_H2);
        paybackYearsCombo.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        paybackYearsCombo.setWidth("85px");
        paybackYearsCombo.setEmptySelectionAllowed(false);
        HorizontalLayout layout = new HorizontalLayout(label, paybackYearsCombo);
        layout.setComponentAlignment(paybackYearsCombo, Alignment.MIDDLE_RIGHT);
        return layout;
    }
    
    private void paybackYearsChanged(int paybackYears) {
        logger.debug("Payback years combo is set to " + paybackYears);
        loanSelector1.setPaybackYears(paybackYears);
        loanSelector2.setPaybackYears(paybackYears);
    }
    
    private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }
    
    private Component createExistingLoansPanel(double yearlyDebtServiceForExistingLoans) {
        Label stlabel = new Label("Short term loan");
        stlabel.setWidth("115px");
        AmountField stLoanField = new AmountField(null);
        stLoanField.setAmount(client.existingLoans.shortTermLoans);
        stLoanField.setWidth("60px");
        stLoanField.setReadOnly(true);
        HorizontalLayout stLayout = new HorizontalLayout(stlabel, stLoanField, stLoanRefinanceCheck);

        Label ltlabel = new Label("Long term loan");
        ltlabel.setWidth("115px");
        AmountField ltLoanField = new AmountField(null);
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
        
        double justifiableShortTermLoan = client.balanceSheet.calculateJustifiableShortTermLoan(loanCalculator.riskParameters.haircuts);
        double maxLongTermloan = loanCalculator.calculate(client, maxLoanDuration, justifiableShortTermLoan, FreeCashFlowCalculator.average).maxLongTermLoan;
        
        Label stlabel = new Label("Ideal short term loan");
        stlabel.setWidth("150px");
        AmountField stLoanField = new AmountField(null);
        stLoanField.setAmount((long)justifiableShortTermLoan);
        stLoanField.setWidth("60px");
        stLoanField.setReadOnly(true);
        HorizontalLayout stLayout = new HorizontalLayout(stlabel, stLoanField);

        Label ltlabel = new Label("Ideal long term loan");
        ltlabel.setWidth("150px");
        AmountField ltLoanField = new AmountField(null);
        ltLoanField.setAmount((long)maxLongTermloan);
        ltLoanField.setWidth("60px");
        ltLoanField.setReadOnly(true);
        HorizontalLayout ltLayout = new HorizontalLayout(ltlabel, ltLoanField);        
        VerticalLayout layout = new VerticalLayout(stLayout, ltLayout);
        Panel panel = new Panel("Ideal structure for " + maxLoanDuration + " years", layout);
        panel.setSizeUndefined();
        
        return panel;
    }
    
    private void submit() {
        super.close();
        UI.getCurrent().addWindow(new DecisionWindow(client));
    }
    
    @Override
    public void close() {
        logger.debug("Proposal window is closed");
        super.close();
    }
    
}
