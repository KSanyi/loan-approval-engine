package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class PercentField extends TextField {
    
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final DecimalFormat FORMATTTER = new DecimalFormat("0.0%");
    
    private double percentValue;
    
    private final String name;
    
    public PercentField(String caption) {
        this(caption, caption);
    }
    
    public PercentField(String caption, String name) {
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
            percentValue = FORMATTTER.parse(value).doubleValue();
            setPercent(percentValue, event.isUserOriginated());
        } catch(ParseException ex) {
            setPercent(createNumber(value), event.isUserOriginated());
        }
    }
    
    private void setPercent(Double percent, boolean userOriginated) {
        if(userOriginated) {
            logger.debug("USERACTION {}: {} is set to {}", LaeUI.currentUser(), name, percent);
        }
        this.percentValue = percent;
        doSetValue(FORMATTTER.format(percent));
    }
    
    private static double createNumber(String value) {
        String clearedValue = value.replaceAll(",", ".").replaceAll("[^\\d.]", "");
        while(StringUtils.countMatches(clearedValue, ".") > 1) {
            clearedValue = clearedValue.replaceFirst("\\.", "");
        }
        if(clearedValue.isEmpty()) {
            clearedValue = "0";
        }
        return Double.parseDouble(clearedValue) / 100;
    }
    
    public Double getPercent() {
        return percentValue;
    }
    
    public void setPercent(Double percent) {
        setPercent(percent, false);
    }
    
}
