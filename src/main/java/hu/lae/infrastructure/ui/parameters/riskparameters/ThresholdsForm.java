package hu.lae.infrastructure.ui.parameters.riskparameters;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.infrastructure.ui.component.NumberField;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
class ThresholdsForm extends CustomField<Thresholds> {

    private final PercentField equityRatioThresholdField = new PercentField("Equity ratio");
    
    private final NumberField liquidityRatioThresholdField = new NumberField("Liquidity ratio");
    
    private final PercentField turnoverReqToleranceField = new PercentField("Turnover Req. tolerance");
    
    private final PercentField debtCapacityField = new PercentField("Debt capacity limit");
    
    private final PercentField localLoanThresholdField = new PercentField("Esrte loan limit");
    
    private final OwnEquityThresholdsForm ownEquityThresholdsForm;
    
    private final PercentField ownEquityRatioThreshold1Field = new PercentField("Own equity r threshold 1");
	private final PercentField ownEquityRatioThreshold2Field = new PercentField("Own equity r threshold 2");
    
    ThresholdsForm(Thresholds thresholds) {
    
        equityRatioThresholdField.setPercent(thresholds.equityRatio);
        liquidityRatioThresholdField.setNumber(thresholds.liquidityRatio);
        turnoverReqToleranceField.setPercent(thresholds.turnoverRequirement);
        debtCapacityField.setPercent(thresholds.debtCapacity);
        localLoanThresholdField.setPercent(thresholds.localLoanRatio);
        
        ownEquityThresholdsForm = new OwnEquityThresholdsForm(thresholds.ownEquityRatioThresholds);
        
        ownEquityRatioThreshold1Field.setPercent(thresholds.ownEquityRatioThresholds.threshold1);
        ownEquityRatioThreshold2Field.setPercent(thresholds.ownEquityRatioThresholds.threshold2);
    }
    
    @Override
    public Thresholds getValue() {
        return new Thresholds(equityRatioThresholdField.getPercent(), liquidityRatioThresholdField.getNumber(),
                turnoverReqToleranceField.getPercent(), debtCapacityField.getPercent(), localLoanThresholdField.getPercent(),
                ownEquityThresholdsForm.getValue());
    }

    @Override
    protected Component initContent() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(false);
        formLayout.setMargin(false);
        
        Label label = new Label("");
        
        formLayout.addComponents(equityRatioThresholdField, liquidityRatioThresholdField, turnoverReqToleranceField, debtCapacityField, localLoanThresholdField,
        		label, ownEquityThresholdsForm);
        
        ownEquityRatioThreshold1Field.setDescription("If the own equity ratio is below this percent of the industrial average the loan maturity must be max 3 years", ContentMode.HTML);
        ownEquityRatioThreshold2Field.setDescription("If the own equity ratio is below this percent of the industrial average the loan maturity must be max 1 year", ContentMode.HTML);
        
        VerticalLayout layout = new VerticalLayout(formLayout, ownEquityThresholdsForm);
        layout.setSpacing(false);
        
        Panel panel = new Panel("Thresholds", layout);
        panel.addStyleName("colored");
        
        return panel;
    }

    @Override
    protected void doSetValue(Thresholds value) {
        throw new IllegalStateException();
    }
    
}
