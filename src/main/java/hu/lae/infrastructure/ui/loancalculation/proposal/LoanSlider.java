package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
public class LoanSlider extends CustomField<Double> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final AmountField amountField = createAmountField();
    
    private final Slider slider = createSlider();
    
    private final Label maxAmountLabel = createMaxAmountLabel();
    
    private double loanValue;
    
    LoanSlider(String caption) {
        setCaption(caption);
        slider.setValue(0d);
        amountField.setValue("0");
        slider.addValueChangeListener(this::valueChanged);
    }
    
    private void valueChanged(ValueChangeEvent<Double> event) {
        double value = event.getValue();
        
        if(event.isUserOriginated()) {
            logger.debug("USERACTION: " + getCaption() + " is set to " + value);
        }
        
        setValue(value);
    }
    
    private AmountField createAmountField() {
        AmountField amountField = new AmountField(null, "loan amount");
        amountField.setWidth("55");
        amountField.setValueChangeMode(ValueChangeMode.LAZY);
        amountField.removeStyleName(ValoTheme.TEXTFIELD_SMALL);
        amountField.addValueChangeListener(v -> {
            try {
                double doubleValue = Double.parseDouble(v.getValue());
                setValue(doubleValue);
            } catch(NumberFormatException ex) {
                setValue(0d);
            }
        });
        return amountField;
    }
    
    private Slider createSlider() {
        Slider slider = new Slider();
        slider.setOrientation(SliderOrientation.HORIZONTAL);
        slider.setWidth("300px");
        return slider;
    }
    
    private Label createMaxAmountLabel() {
        Label label = new Label("<center>Max</center>0 million Ft", ContentMode.HTML);
        return label;
    }
    
    @Override
    public Double getValue() {
        return loanValue;
    }

    @Override
    protected Component initContent() {
        HorizontalLayout layout = new HorizontalLayout(amountField, slider, maxAmountLabel);
        layout.setSpacing(false);
        return layout;
    }

    @Override
    protected void doSetValue(Double value) {
        if(value > slider.getMax()) {
            value = slider.getMax();
        }
        if(value < 0) {
            value = 0d;
        }
        if(Math.abs(value - loanValue) > 1) {
            loanValue = value;
            slider.setValue(value);
            amountField.setValue(value + "");
        }
    }
    
    void setMaxLoanValue(double maxLoanValue) {
        if(maxLoanValue < loanValue) {
            doSetValue(maxLoanValue);
        }
        slider.setMax(maxLoanValue);
        maxAmountLabel.setValue("<center>Max</center>" + Formatters.formateAmount(maxLoanValue) + " million Ft" );
    }
    
}
