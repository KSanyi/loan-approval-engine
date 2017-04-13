package hu.lae.infrastructure.ui.component;

import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class PercentField extends TextField {

    //private final DecimalFormat PF = new DecimalFormat("0.0%");
    
    public PercentField(String caption) {
        super(caption);
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
    }
    
    public Double getPercent() {
        return Double.parseDouble(getValue());
    }
    
    public void setPercent(Double number) {
        setValue(String.valueOf(number));
    }
    
}
