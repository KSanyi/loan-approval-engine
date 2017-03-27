package hu.lae.infrastructure.ui.loancalculation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.IncomeStatement;
import hu.lae.loan.LoanApplicationResult;
import hu.lae.loan.LoanCalculator;

@SuppressWarnings("serial")
public class CalculatorWindow extends Window {

    private static final Integer DEFAULT_PAYBACK_YEARS = 5;
    
    private final LoanCalculator loanCalculator;
    
    private final BalanceSheet balanceSheet;
    
    private final IncomeStatement incomeStatement;
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");

    private final ComboBox paybackYearsCombo;
 
    //private final Button checkCalculationButton = new Button("Check calculations");
    
    public CalculatorWindow(LoanCalculator loanCalculator, BalanceSheet balanceSheet, IncomeStatement incomeStatement) {
        this.loanCalculator = loanCalculator;
        this.balanceSheet = balanceSheet;
        this.incomeStatement = incomeStatement;
        setModal(true);
        paybackYearsCombo = new ComboBox(null, generateComboValues(loanCalculator.riskParameters.maxLoanDuration));
        paybackYearsCombo.addValueChangeListener(value -> paybackYearsChanged((Integer)value.getProperty().getValue()));
        paybackYearsCombo.setValue(DEFAULT_PAYBACK_YEARS);
        stLoanSlider.addLoanValueChangeListener(loanValue -> shortTermloanChanged(loanValue));
        //checkCalculationButton.addStyleName(ValoTheme.BUTTON_LINK);
        //checkCalculationButton.addClickListener(click -> UI.getCurrent().addWindow(new CalculationsWindow()));
        
        createLayout();
        
        paybackYearsChanged(DEFAULT_PAYBACK_YEARS);
    }
    
    private void createLayout() {
        setWidth("900px");
        setResizable(false);
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        Component component = createPaybackYearsField();
        
        long justifiableShortTermLoan = (long)loanCalculator.calculate(balanceSheet, incomeStatement, DEFAULT_PAYBACK_YEARS, 0).justifiableShortTermLoan;
        stLoanSlider.setDescription("Justifiable short term loan:<br/><center>" + justifiableShortTermLoan + " million Ft</center>");
        
        layout.addComponents(component, stLoanSlider, ltLoanSlider);
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        Panel panel = new Panel("Calculation", layout);
        panel.addStyleName("colored");
        setContent(panel);
    }
    
    private HorizontalLayout createPaybackYearsField() {
        Label label = new Label("Payback years");
        label.addStyleName(ValoTheme.LABEL_H2);
        paybackYearsCombo.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        paybackYearsCombo.addStyleName(ValoTheme.COMBOBOX_LARGE);
        paybackYearsCombo.setWidth("85px");
        paybackYearsCombo.setNullSelectionAllowed(false);
        HorizontalLayout layout = new HorizontalLayout(label, paybackYearsCombo);
        layout.setSpacing(true);
        return layout;
    }
    
    private void paybackYearsChanged(int paybackYears) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, paybackYears, stLoanSlider.getLoanValue());
        stLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxShortTermLoan);
        stLoanSlider.setLoanValue((long)loanApplicationResult.justifiableShortTermLoan);
        ltLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxLongTermLoan);
    }
    
    private void shortTermloanChanged(long shortTermLoan) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, (Integer)paybackYearsCombo.getValue(), shortTermLoan);
        ltLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxLongTermLoan);
    }
    
    private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }
    
}
