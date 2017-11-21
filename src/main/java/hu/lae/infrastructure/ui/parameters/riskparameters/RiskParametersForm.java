package hu.lae.infrastructure.ui.parameters.riskparameters;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.infrastructure.ui.component.NumberField;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class RiskParametersForm extends CustomField<RiskParameters> {

    private final RiskParameters riskParameters;
    
    private final PercentField amortizationRateField = new PercentField("Amortization rate");
    private final PercentField arField = new PercentField("A/R justifiable ratio");
    private final PercentField stockField = new PercentField("Stock justifiable ratio");
    private final PercentField cashField = new PercentField("Cash justifiable ratio");
    private final PercentField otherField = new PercentField("Other justifiable ratio");
    private final NumberField dscrThresholdField = new NumberField("DSCR threshold");
    private final PercentField shortTermInterestRateField = new PercentField("Short term interest rate");
    private final IndustryMaxLoanDurationsForm industryMaxLoanDurationsForm;
    private final PercentField longTermInterestRateField = new PercentField("Long term interest rate");
    private final ThresholdsForm thresholdsForm;
    private final CollateralRequirementsForm collateralRequirementsForm;
    private final EbitdaCorrectionParametersPanel ebitdaCorrectionParametersForm;

    public RiskParametersForm(RiskParameters riskParameters) {
        this.riskParameters = riskParameters;
        
        industryMaxLoanDurationsForm = new IndustryMaxLoanDurationsForm(riskParameters.industryMaxLoanDurations);
        thresholdsForm = new ThresholdsForm(riskParameters.thresholds);
        collateralRequirementsForm = new CollateralRequirementsForm(riskParameters.collateralRequirement);
        ebitdaCorrectionParametersForm = new EbitdaCorrectionParametersPanel(riskParameters.ebitdaCorrectionParameters);
        
        amortizationRateField.setPercent(riskParameters.amortizationRate);
        dscrThresholdField.setNumber(riskParameters.dscrThreshold);
        shortTermInterestRateField.setPercent(riskParameters.interestRates.shortTermInterestRate.value);
        longTermInterestRateField.setPercent(riskParameters.interestRates.longTermInterestRate.value);
        
        arField.setPercent(riskParameters.haircuts.accountsReceivable);
        stockField.setPercent(riskParameters.haircuts.stock);
        cashField.setPercent(riskParameters.haircuts.cash);
        otherField.setPercent(riskParameters.haircuts.other);
    }
    
	@Override
	public RiskParameters getValue() {
		return new RiskParameters(riskParameters.id, riskParameters.name, 
                amortizationRateField.getPercent(), 
                new Haircuts(arField.getPercent(), stockField.getPercent(), cashField.getPercent(),  otherField.getPercent()),
                new InterestRates(shortTermInterestRateField.getPercent(), longTermInterestRateField.getPercent()),
                industryMaxLoanDurationsForm.getValue(),
                dscrThresholdField.getNumber(),
                thresholdsForm.getValue(),
                collateralRequirementsForm.getValue(),
                ebitdaCorrectionParametersForm.getEbitdaCorrectionParameters());
	}

	@Override
	protected Component initContent() {
		
		FormLayout layout = new FormLayout(amortizationRateField, arField, stockField, cashField, otherField, dscrThresholdField, shortTermInterestRateField, longTermInterestRateField);
        layout.setSpacing(false);
        layout.setMargin(new MarginInfo(false, true));
        
        Panel panel = new Panel("Basic", layout);
        panel.addStyleName("colored");
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.setSpacing(true);
		gridLayout.setColumns(4);
		
		gridLayout.addComponents(panel, industryMaxLoanDurationsForm, thresholdsForm, collateralRequirementsForm, ebitdaCorrectionParametersForm);
        
		return gridLayout;
	}

	@Override
	protected void doSetValue(RiskParameters value) {
		throw new IllegalStateException();
	}
    
}
