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

import hu.lae.infrastructure.ui.LaeUI;

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
        try {
            long numberValue = FORMATTER.parse(value).longValue();
            setAmount(numberValue, event.isUserOriginated());
        } catch(ParseException ex) {
            setAmount(createNumber(value), event.isUserOriginated());
        }
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
    
    public long getAmount() {
        return amount;
    }
    
    private void setAmount(Long amount, boolean userOriginated) {
        if(userOriginated) {
            logger.debug("USERACTION {}: {} is set to {}", LaeUI.currentUser(), name, amount);
        }
        this.amount = amount;
        doSetValue(FORMATTER.format(amount));
    }
    
    public void setAmount(Long amount) {
        setAmount(amount, false);
    }
    
}
