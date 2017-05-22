package hu.lae.infrastructure.ui.riskparameters;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.NumberField;
import hu.lae.riskparameters.Thresholds;

@SuppressWarnings("serial")
class ThresholdsForm extends CustomField<Thresholds> {

    private final NumberField equityRatioThresholdField = new NumberField("Equity ratio");
    
    private final NumberField liquidityRatioThresholdField = new NumberField("Liquidity ratio");
    
    ThresholdsForm(Thresholds thresholds) {
    
        setCaption("Thresholds");
        
        equityRatioThresholdField.setNumber(thresholds.equityRatio);
        liquidityRatioThresholdField.setNumber(thresholds.liquidityRatio);
    }
    
    @Override
    public Thresholds getValue() {
        return new Thresholds(equityRatioThresholdField.getNumber(), liquidityRatioThresholdField.getNumber());
    }

    @Override
    protected Component initContent() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(false);
        formLayout.setMargin(false);
        
        equityRatioThresholdField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        liquidityRatioThresholdField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        
        formLayout.addComponents(equityRatioThresholdField, liquidityRatioThresholdField);
        formLayout.setComponentAlignment(equityRatioThresholdField, Alignment.MIDDLE_LEFT);
        formLayout.setComponentAlignment(liquidityRatioThresholdField, Alignment.MIDDLE_LEFT);
        
        return formLayout;
    }

    @Override
    protected void doSetValue(Thresholds value) {
        throw new IllegalStateException();
    }
    
}
