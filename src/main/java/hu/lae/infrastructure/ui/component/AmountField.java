package hu.lae.infrastructure.ui.component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class AmountField extends TextField {

    private static final DecimalFormat FORMAT;
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator((char) 160);
        FORMAT = new DecimalFormat("###,###", decimalFormatSymbols);
    }
    
    private long amount;
    
    public AmountField(String caption) {
        super(caption);
        
        setWidth("90px");   
        setMaxLength(8);
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        
        addValueChangeListener(event -> valueChanged(event.getValue()));
    }
    
    private void valueChanged(String value) {
        try {
            long numberValue = FORMAT.parse(value).longValue();
            setAmount(numberValue);
        } catch(ParseException ex) {
            String clearedValue = value.replaceAll(",", ".").replaceAll("[^\\d.]", "");
            setValue(clearedValue.isEmpty() ? "0" : clearedValue);
        }
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
        doSetValue(FORMAT.format(amount));
    }
    
}
