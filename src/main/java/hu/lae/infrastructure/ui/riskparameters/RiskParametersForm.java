package hu.lae.infrastructure.ui.riskparameters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.NumberField;
import hu.lae.infrastructure.ui.component.PercentField;
import hu.lae.riskparameters.Haircuts;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;

@SuppressWarnings("serial")
public class RiskParametersForm extends FormLayout {

    private final RiskParameters riskParameters;
    
    private final PercentField amortizationRateField = new PercentField("Amortization rate");
    private final PercentField arField = new PercentField("A/R justifiable ratio");
    private final PercentField stockField = new PercentField("Stock justifiable ratio");
    private final PercentField cashField = new PercentField("Cash justifiable ratio");
    private final PercentField otherField = new PercentField("Other justifiable ratio");
    private final NumberField dscrThresholdField = new NumberField("DSCR threshold");
    private final PercentField shortTermInterestRateField = new PercentField("Short term interest rate");
    private final ComboBox maxLoanDurationCombo = new ComboBox("Max loan duration years", generateComboValues());
    private final PercentField longTermInterestRateField = new PercentField("Long term interest rate");

    public RiskParametersForm(RiskParameters riskParameters) {
        this.riskParameters = riskParameters;

        setSpacing(false);
        setMargin(false);
        addComponents(amortizationRateField, arField, stockField, cashField, otherField, dscrThresholdField, shortTermInterestRateField, longTermInterestRateField, maxLoanDurationCombo);
        
        maxLoanDurationCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        maxLoanDurationCombo.setNullSelectionAllowed(false);
        maxLoanDurationCombo.setWidth("60px");
        
        amortizationRateField.setNumber(riskParameters.amortizationRate);
        dscrThresholdField.setNumber(riskParameters.dscrThreshold);
        shortTermInterestRateField.setNumber(riskParameters.shortTermInterestRate.value);
        maxLoanDurationCombo.setValue(riskParameters.maxLoanDuration);
        longTermInterestRateField.setNumber(riskParameters.longTermInterestRate.value);
        
        arField.setNumber(riskParameters.haircuts.accountsReceivable);
        stockField.setNumber(riskParameters.haircuts.stock);
        cashField.setNumber(riskParameters.haircuts.cash);
        otherField.setNumber(riskParameters.haircuts.other);
    }
    
    public RiskParameters getRiskParameters() {
        return new RiskParameters(riskParameters.id, riskParameters.name, 
                amortizationRateField.getPercent(), 
                new Haircuts(arField.getPercent(), stockField.getPercent(), cashField.getPercent(),  otherField.getPercent()),
                new InterestRate(shortTermInterestRateField.getPercent()),
                (int)maxLoanDurationCombo.getValue(),
                new InterestRate(longTermInterestRateField.getPercent()),
                dscrThresholdField.getNumber());
    }
    
    private static List<Integer> generateComboValues() {
        return IntStream.range(1, 31).mapToObj(i -> i).collect(Collectors.toList());
    }
    
}
