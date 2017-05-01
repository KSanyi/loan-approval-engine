package hu.lae.infrastructure.ui.component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class AmountField extends TextField {

    private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);
    
    private static final DecimalFormat FORMATTTER;
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator((char) 160);
        FORMATTTER = new DecimalFormat("###,###", decimalFormatSymbols);
    }
    
    private long amount;
    
    private String name;
    
    public AmountField(String caption) {
        this(caption, "noname");
    }
    
    public AmountField(String caption, String name) {
        super(caption);
        this.name = name;
        setWidth("90px");   
        setMaxLength(8);
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        addValueChangeListener(event -> valueChanged(event.getValue()));
        setValueChangeMode(ValueChangeMode.BLUR);
    }
    
    @Override
    public boolean setValue(String value, boolean userOriginated) {
        if(userOriginated) {
            logger.debug(name + " is set to " + value);
        }
        return super.setValue(value, userOriginated);
    }
    
    private void valueChanged(String value) {
        try {
            long numberValue = FORMATTTER.parse(value).longValue();
            setAmount(numberValue);
        } catch(ParseException ex) {
            setAmount(createNumber(value));
        }
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
        doSetValue(FORMATTTER.format(amount));
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
