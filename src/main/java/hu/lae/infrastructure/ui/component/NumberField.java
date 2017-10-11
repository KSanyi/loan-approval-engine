package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class NumberField extends TextField {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private double number;
    
    private final String name;
    
    public NumberField(String caption) {
        this(caption, caption);
    }
    
    public NumberField(String caption, String name) {
        super(caption);
        this.name = name;
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        addValueChangeListener(event -> valueChanged(event));
        setValueChangeMode(ValueChangeMode.BLUR);
    }
    
    private void valueChanged(ValueChangeEvent<String> event) {
        String value = event.getValue();
        try {
            double doubleValue = Double.parseDouble(value);
            setNumber(doubleValue, event.isUserOriginated());
        } catch(NumberFormatException ex) {
            setNumber(createNumber(value), event.isUserOriginated());
        }
    }
    
    private void setNumber(Double number, boolean userOriginated) {
        if(userOriginated) {
            logger.debug("USERACTION {}: {} is set to {}", LaeUI.currentUser(), name, number);
        }
        this.number = number;
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
    
    public double getNumber() {
        return number;
    }
    
    public void setNumber(Double number) {
        setNumber(number, false);
    }
    
}
