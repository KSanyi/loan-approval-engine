package hu.lae.infrastructure.ui.loancalculation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.IncomeStatement;
import hu.lae.loan.ExistingLoans;
import hu.lae.loan.LoanApplicationResult;
import hu.lae.loan.LoanCalculator;

@SuppressWarnings("serial")
public class CalculatorWindow extends Window {

    private static final Integer DEFAULT_PAYBACK_YEARS = 5;
    
    private final LoanCalculator loanCalculator;
    private final BalanceSheet balanceSheet;
    private final IncomeStatement incomeStatement;
    private final ExistingLoans existingLoans;    
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");

    private final ComboBox<Integer> paybackYearsCombo;
 
    //private final Button checkCalculationButton = new Button("Check calculations");
    
    public CalculatorWindow(LoanCalculator loanCalculator, BalanceSheet balanceSheet, IncomeStatement incomeStatement, ExistingLoans existingLoans, LocalDate currentDate) {
        this.loanCalculator = loanCalculator;
        this.balanceSheet = balanceSheet;
        this.incomeStatement = incomeStatement;
        this.existingLoans = existingLoans;
        
        setModal(true);
        paybackYearsCombo = new ComboBox<>(null, generateComboValues(loanCalculator.riskParameters.maxLoanDuration));
        paybackYearsCombo.setValue(Math.min(DEFAULT_PAYBACK_YEARS, loanCalculator.riskParameters.maxLoanDuration));
        paybackYearsCombo.addValueChangeListener(value -> paybackYearsChanged(value.getValue()));
        stLoanSlider.addLoanValueChangeListener(loanValue -> shortTermloanChanged(loanValue));
        //checkCalculationButton.addStyleName(ValoTheme.BUTTON_LINK);
        //checkCalculationButton.addClickListener(click -> UI.getCurrent().addWindow(new CalculationsWindow()));
        
        double yearlyDebtServiceForExistingLoans = existingLoans.yealyDebtService(loanCalculator.riskParameters.longTermInterestRate, currentDate);
        setContent(createLayout(yearlyDebtServiceForExistingLoans));
        
        paybackYearsChanged(DEFAULT_PAYBACK_YEARS);
    }
    
    private Component createLayout(double yearlyDebtServiceForExistingLoans) {
        setWidth("900px");
        setResizable(false);
        VerticalLayout layout = new VerticalLayout();
        Component component = createPaybackYearsField();
        
        layout.addComponents(component, stLoanSlider, ltLoanSlider, createExistingDebtServiceField(yearlyDebtServiceForExistingLoans));
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        Panel panel = new Panel("Calculation", layout);
        panel.addStyleName("colored");
        return panel;
    }
    
    private Component createExistingDebtServiceField(double yearlyDebtServiceForExistingLoans) {
        Label label = new Label("Yearly Debt Service For Existing Loans:");
        TextField textField = new TextField();
        textField.setValue(String.format("%.2f", yearlyDebtServiceForExistingLoans));
        textField.setReadOnly(true);
        textField.setWidth("100px");
        textField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
        HorizontalLayout layout = new HorizontalLayout(label, textField);
        return layout;
    }
    
    private HorizontalLayout createPaybackYearsField() {
        Label label = new Label("Payback years");
        label.addStyleName(ValoTheme.LABEL_H2);
        paybackYearsCombo.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        paybackYearsCombo.addStyleName(ValoTheme.COMBOBOX_LARGE);
        paybackYearsCombo.setWidth("85px");
        paybackYearsCombo.setEmptySelectionAllowed(false);
        HorizontalLayout layout = new HorizontalLayout(label, paybackYearsCombo);
        return layout;
    }
    
    private void paybackYearsChanged(int paybackYears) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, existingLoans, paybackYears, stLoanSlider.getLoanValue());
        stLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxShortTermLoan);
        stLoanSlider.setLoanValue((long)loanApplicationResult.justifiableShortTermLoan);
        ltLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxLongTermLoan);
        stLoanSlider.setDescription("Justifiable short term loan:<br/><center>" + loanApplicationResult.justifiableShortTermLoan + " million Ft</center>");
    }
    
    private void shortTermloanChanged(long shortTermLoan) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, existingLoans, (Integer)paybackYearsCombo.getValue(), shortTermLoan);
        ltLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxLongTermLoan);
    }
    
    private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }
    
}
