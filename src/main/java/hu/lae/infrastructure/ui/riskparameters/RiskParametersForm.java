package hu.lae.infrastructure.ui.riskparameters;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

import hu.lae.infrastructure.ui.component.NumberField;
import hu.lae.infrastructure.ui.component.PercentField;
import hu.lae.riskparameters.Haircuts;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;

@SuppressWarnings("serial")
public class RiskParametersForm extends VerticalLayout {

    private final RiskParameters riskParameters;
    
    private final PercentField amortizationRateField = new PercentField("Amortization rate");
    private final PercentField arField = new PercentField("A/R justifiable ratio");
    private final PercentField stockField = new PercentField("Stock justifiable ratio");
    private final PercentField cashField = new PercentField("Cash justifiable ratio");
    private final PercentField otherField = new PercentField("Other justifiable ratio");
    private final NumberField dscrThresholdField = new NumberField("DSCR threshold");
    private final PercentField shortTermInterestRateField = new PercentField("Short term interest rate");
    private final MaxLoanDurationsForm maxLoanDurationsForm;
    private final PercentField longTermInterestRateField = new PercentField("Long term interest rate");

    public RiskParametersForm(RiskParameters riskParameters) {
        this.riskParameters = riskParameters;

        maxLoanDurationsForm = new MaxLoanDurationsForm(riskParameters.maxLoanDurations);
        
        setSpacing(false);
        setMargin(false);
        FormLayout layout = new FormLayout(amortizationRateField, arField, stockField, cashField, otherField, dscrThresholdField, shortTermInterestRateField, longTermInterestRateField);
        layout.setSpacing(false);
        layout.setMargin(false);
        addComponents(layout, maxLoanDurationsForm);
        
        amortizationRateField.setPercent(riskParameters.amortizationRate);
        dscrThresholdField.setNumber(riskParameters.dscrThreshold);
        shortTermInterestRateField.setPercent(riskParameters.shortTermInterestRate.value);
        longTermInterestRateField.setPercent(riskParameters.longTermInterestRate.value);
        
        arField.setPercent(riskParameters.haircuts.accountsReceivable);
        stockField.setPercent(riskParameters.haircuts.stock);
        cashField.setPercent(riskParameters.haircuts.cash);
        otherField.setPercent(riskParameters.haircuts.other);
        
    }
    
    public RiskParameters getRiskParameters() {
        return new RiskParameters(riskParameters.id, riskParameters.name, 
                amortizationRateField.getPercent(), 
                new Haircuts(arField.getPercent(), stockField.getPercent(), cashField.getPercent(),  otherField.getPercent()),
                new InterestRate(shortTermInterestRateField.getPercent()),
                maxLoanDurationsForm.getMaxLoanDurations(),
                new InterestRate(longTermInterestRateField.getPercent()),
                dscrThresholdField.getNumber());
    }
    
}
