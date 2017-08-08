package hu.lae.infrastructure.ui.riskparameters;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.infrastructure.ui.component.NumberField;

@SuppressWarnings("serial")
class ThresholdsForm extends CustomField<Thresholds> {

    private final NumberField equityRatioThresholdField = new NumberField("Equity ratio");
    
    private final NumberField liquidityRatioThresholdField = new NumberField("Liquidity ratio");
    
    private final NumberField turnoverReqToleranceField = new NumberField("Turnover Req. tolerance");
    
    private final NumberField debtCapacityField = new NumberField("Debt capacity limit");
    
    private final NumberField localLoanThresholdField = new NumberField("Esrte loan limit");
    
    ThresholdsForm(Thresholds thresholds) {
    
        setCaption("Thresholds");
        
        equityRatioThresholdField.setNumber(thresholds.equityRatio);
        liquidityRatioThresholdField.setNumber(thresholds.liquidityRatio);
        turnoverReqToleranceField.setNumber(thresholds.turnoverRequirement);
        debtCapacityField.setNumber(thresholds.debtCapacity);
        localLoanThresholdField.setNumber(thresholds.localLoanRatio);
    }
    
    @Override
    public Thresholds getValue() {
        return new Thresholds(equityRatioThresholdField.getNumber(), liquidityRatioThresholdField.getNumber(),
                turnoverReqToleranceField.getNumber(), debtCapacityField.getNumber(), localLoanThresholdField.getNumber());
    }

    @Override
    protected Component initContent() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(false);
        formLayout.setMargin(false);
        
        equityRatioThresholdField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        liquidityRatioThresholdField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        turnoverReqToleranceField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        debtCapacityField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        localLoanThresholdField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        
        formLayout.addComponents(equityRatioThresholdField, liquidityRatioThresholdField, turnoverReqToleranceField, debtCapacityField, localLoanThresholdField);
        formLayout.setComponentAlignment(equityRatioThresholdField, Alignment.MIDDLE_LEFT);
        formLayout.setComponentAlignment(liquidityRatioThresholdField, Alignment.MIDDLE_LEFT);
        formLayout.setComponentAlignment(turnoverReqToleranceField, Alignment.MIDDLE_LEFT);
        formLayout.setComponentAlignment(debtCapacityField, Alignment.MIDDLE_LEFT);
        formLayout.setComponentAlignment(localLoanThresholdField, Alignment.MIDDLE_LEFT);
        
        return formLayout;
    }

    @Override
    protected void doSetValue(Thresholds value) {
        throw new IllegalStateException();
    }
    
}
