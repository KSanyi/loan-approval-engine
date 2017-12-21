package hu.lae.infrastructure.ui.parameters.riskparameters;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;

import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class EbitdaCorrectionParametersPanel extends Panel {

    private final PercentField reasonableEbitdaMarginGrowthField = new PercentField("Reasonable EBITDA growth");
    private final PercentField maxDeltaField = new PercentField("Max delta");
    private final PercentField minXXXField = new PercentField("Min xxx");
    private final PercentField maxEbitdaDecreaseField = new PercentField("Max EBITDA decrease");
    private final YearlyWeightsField yearlyWeightsField;

    public EbitdaCorrectionParametersPanel(EbitdaCorrectionParameters ebitdaCorrectionParameters) {
        
        setCaption("EBITDA Correction Parameters");
        
        yearlyWeightsField = new YearlyWeightsField(ebitdaCorrectionParameters.yearlyWeights);
        
        FormLayout layout = new FormLayout(reasonableEbitdaMarginGrowthField, maxDeltaField, minXXXField, maxEbitdaDecreaseField, yearlyWeightsField);
        layout.setSpacing(false);
        layout.setMargin(new MarginInfo(false, true));
        setContent(layout);
        
        setSizeUndefined();
        
        addStyleName("colored");
        
        reasonableEbitdaMarginGrowthField.setPercent(ebitdaCorrectionParameters.reasonableEbitdaMarginGrowth);
        maxDeltaField.setPercent(ebitdaCorrectionParameters.maxDelta);
        minXXXField.setPercent(ebitdaCorrectionParameters.minXXX);
        maxEbitdaDecreaseField.setPercent(ebitdaCorrectionParameters.maxEbitdaDecrease);
    }
    
    public EbitdaCorrectionParameters getEbitdaCorrectionParameters() {
        return new EbitdaCorrectionParameters(reasonableEbitdaMarginGrowthField.getPercent(),
                maxDeltaField.getPercent(),
                minXXXField.getPercent(),
                maxEbitdaDecreaseField.getPercent(),
                yearlyWeightsField.getValue());
    }
    
}
