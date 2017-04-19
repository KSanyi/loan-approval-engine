package hu.lae.infrastructure.ui.loancalculation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.Client;
import hu.lae.infrastructure.ui.LaeUI;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.loan.LoanApplicationResult;
import hu.lae.loan.LoanCalculator;

@SuppressWarnings("serial")
public class CalculatorWindow extends Window {
    
    private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);

    private static final Integer DEFAULT_PAYBACK_YEARS = 5;
    
    private final LoanCalculator loanCalculator;
    private final Client client;
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");

    private final ComboBox<Integer> paybackYearsCombo;
 
    //private final Button checkCalculationButton = new Button("Check calculations");
    
    public CalculatorWindow(LoanCalculator loanCalculator, Client client, LocalDate currentDate) {
        this.loanCalculator = loanCalculator;
        this.client = client;
        
        setModal(true);
        int maxLoanDuration = loanCalculator.riskParameters.maxLoanDurations.maxLoanDuration(client.industry); 
        paybackYearsCombo = new ComboBox<>(null, generateComboValues(maxLoanDuration));
        paybackYearsCombo.setValue(Math.min(DEFAULT_PAYBACK_YEARS, maxLoanDuration));
        paybackYearsCombo.addValueChangeListener(value -> paybackYearsChanged(value.getValue()));
        stLoanSlider.addLoanValueChangeListener(loanValue -> shortTermloanChanged(loanValue));
        ltLoanSlider.setLoanValue(1);
        ltLoanSlider.setLoanValue(0);
        //checkCalculationButton.addStyleName(ValoTheme.BUTTON_LINK);
        //checkCalculationButton.addClickListener(click -> UI.getCurrent().addWindow(new CalculationsWindow()));
        
        double yearlyDebtServiceForExistingLoans = client.existingLoans.yealyDebtService(loanCalculator.riskParameters.longTermInterestRate, currentDate);
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
        AmountField amountField = new AmountField(null);
        amountField.setAmount((long)yearlyDebtServiceForExistingLoans);
        amountField.setReadOnly(true);
        amountField.setWidth("80px");
        HorizontalLayout layout = new HorizontalLayout(label, amountField);
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
        logger.debug("Payback years combo is set to " + paybackYears);
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYears, stLoanSlider.getLoanValue());
        stLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxShortTermLoan);
        stLoanSlider.setLoanValue((long)loanApplicationResult.justifiableShortTermLoan);
        ltLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxLongTermLoan);
        stLoanSlider.setDescription("Justifiable short term loan:<br/><center>" + loanApplicationResult.justifiableShortTermLoan + " million Ft</center>", ContentMode.HTML);
    }
    
    private void shortTermloanChanged(long shortTermLoan) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, (Integer)paybackYearsCombo.getValue(), shortTermLoan);
        ltLoanSlider.setMaxLoanValue((long)loanApplicationResult.maxLongTermLoan);
    }
    
    private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }
    
    @Override
    public void close() {
        logger.debug("Calculation window is closed");
        super.close();
    }
    
}
