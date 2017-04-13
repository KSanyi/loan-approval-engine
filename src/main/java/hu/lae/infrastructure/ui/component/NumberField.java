package hu.lae.infrastructure.ui.component;

import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class NumberField extends TextField {

    public NumberField(String caption) {
        super(caption);
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        
        addValueChangeListener(event -> valueChanged(event.getValue()));
    }
    
    private void valueChanged(String value) {
        try {
            Double.parseDouble(value);
            doSetValue(value);
        } catch(NumberFormatException ex) {
            String clearedValue = value.replaceAll(",", ".").replaceAll("[^\\d.]", "");
            setValue(clearedValue.isEmpty() ? "0" : clearedValue);
        }
    }
    
    public Double getNumber() {
        return Double.parseDouble(getValue());
    }
    
    public void setNumber(Double number) {
        doSetValue(String.valueOf(number));
    }
    
}
