package hu.lae.infrastructure.ui.parameters.riskparameters;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;

import hu.lae.domain.finance.YearlyData;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class YearlyWeightsField extends CustomField<YearlyData<Double>> {

    private final PercentField tField = new PercentField("T");
    private final PercentField tMinus1Field = new PercentField("T-1");
    private final PercentField tMinus2Field = new PercentField("T-2");
    
    public YearlyWeightsField(YearlyData<Double> value) {
        setCaption("Évek súlyozása");
        tField.setPercent(value.tValue);
        tMinus1Field.setPercent(value.tMinus1Value);
        tMinus2Field.setPercent(value.tMinus2Value);
        
        tField.addValueChangeListener(this::valueChanged);
        tMinus1Field.addValueChangeListener(this::valueChanged);
        tMinus2Field.setReadOnly(true);
    }
    
    private void valueChanged(ValueChangeEvent<String> event) {
        
        PercentField field = (PercentField)event.getComponent();
        if(tField.getPercent() + tMinus1Field.getPercent() > 1) {
            tField.setPercent(1.0);
            tMinus1Field.setPercent(0.0);
        } else if(tField.getPercent() + tMinus1Field.getPercent() < 0) {
            tField.setPercent(1.0);
            tMinus1Field.setPercent(0.0);
        }
        
        tMinus2Field.setReadOnly(false);
        tMinus2Field.setPercent(1 - tField.getPercent() - tMinus1Field.getPercent());
        tMinus2Field.setReadOnly(true);
    }

    @Override
    public YearlyData<Double> getValue() {
        return new YearlyData<Double>(tField.getPercent(), tMinus1Field.getPercent(), tMinus2Field.getPercent());
    }

    @Override
    protected Component initContent() {
        return new HorizontalLayout(tField, tMinus1Field, tMinus2Field);
    }

    @Override
    protected void doSetValue(YearlyData<Double> value) {
        throw new UnsupportedOperationException();
    }

}
