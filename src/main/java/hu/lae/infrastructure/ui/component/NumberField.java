package hu.lae.infrastructure.ui.component;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class NumberField extends TextField {

    private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);
    
    public NumberField(String caption) {
        super(caption);
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        addValueChangeListener(event -> valueChanged(event.getValue()));
        setValueChangeMode(ValueChangeMode.BLUR);
    }
    
    private void valueChanged(String value) {
        try {
            logger.debug(getCaption() + " is set to " + value);
            double doubleValue = Double.parseDouble(value);
            setNumber(doubleValue);
        } catch(NumberFormatException ex) {
            setNumber(createNumber(value));
        }
    }
    
    public Double getNumber() {
        return Double.parseDouble(getValue());
    }
    
    public void setNumber(Double number) {
        doSetValue(String.valueOf(number));
    }
    
    private static double createNumber(String value) {
        String clearedValue = value.replaceAll(",", ".").replaceAll("[^\\d.]", "");
        while(StringUtils.countMatches(clearedValue, ".") > 1) {
            clearedValue = clearedValue.replaceFirst("\\.", "");
        }
        if(clearedValue.isEmpty()) {
            clearedValue = "0";
        }
        return Double.parseDouble(clearedValue);
    }
    
}
