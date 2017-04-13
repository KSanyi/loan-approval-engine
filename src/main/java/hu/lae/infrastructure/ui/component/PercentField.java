package hu.lae.infrastructure.ui.component;

import java.text.DecimalFormat;
import java.text.ParseException;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class PercentField extends TextField {

    private final DecimalFormat PF = new DecimalFormat("0.0%");
    
    private double percentValue;
    
    public PercentField(String caption) {
        super(caption);
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        addValueChangeListener(event -> valueChanged(event.getValue()));
        setValueChangeMode(ValueChangeMode.BLUR);
    }
    
    private void valueChanged(String value) {
        try {
            percentValue = PF.parse(value).doubleValue();
            setPercent(percentValue);
        } catch(ParseException ex) {
            String clearedValue = value.replaceAll(",", ".").replaceAll("[^\\d.]", "") + "%";
            setValue(clearedValue.isEmpty() ? "0" : clearedValue);
        }
    }
    
    public Double getPercent() {
        return percentValue;
    }
    
    public void setPercent(Double percent) {
        this.percentValue = percent;
        doSetValue(PF.format(percent));
    }
    
}
