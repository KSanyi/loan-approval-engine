package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class AmountField extends TextField {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final DecimalFormat FORMATTER;
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator((char) 160);
        FORMATTER = new DecimalFormat("###,###", decimalFormatSymbols);
    }
    
    private long amount;
    
    private String name;
    
    public AmountField(String caption) {
        this(caption, caption);
    }
    
    public AmountField(String caption, String name) {
        super(caption);
        this.name = name;
        setWidth("90px");   
        setMaxLength(8);
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        setAmount(0l);
        addValueChangeListener(event -> valueChanged(event));
        setValueChangeMode(ValueChangeMode.BLUR);
    }
    
    private void valueChanged(ValueChangeEvent<String> event) {
        String value = event.getValue();
        
        if(event.isUserOriginated()) {
            logger.debug("USERACTION: " + name + " is set to " + value);
        }
        
        try {
            long numberValue = FORMATTER.parse(value).longValue();
            setAmount(numberValue);
        } catch(ParseException ex) {
            setAmount(createNumber(value));
        }
    }
    
    public long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
        logger.debug(name + " is: " + amount);
        doSetValue(FORMATTER.format(amount));
    }
    
    private static long createNumber(String value) {
        String clearedValue = value.replaceAll(",", ".").replaceAll("[^\\d.]", "");
        while(StringUtils.countMatches(clearedValue, ".") > 1) {
            clearedValue = clearedValue.replaceFirst("\\.", "");
        }
        if(clearedValue.isEmpty()) {
            clearedValue = "0";
        }
        return (long)Double.parseDouble(clearedValue);
    }
    
}
