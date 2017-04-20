package hu.lae.infrastructure.ui.loancalculation;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.AmountField;

@SuppressWarnings("serial")
public class LoanSlider extends HorizontalLayout {

    private final MySlider slider;
    
    private final Label maxAmountLabel = createMaxAmountLabel();
    
    private final AmountField amountField = createAmountField();
    
    private long minLoanValue = 0;
    
    private final List<LoanValueChangeListener> loanValueChangeListeners = new ArrayList<>();
    
    LoanSlider(String caption) {
        slider = createSlider();
        
        addComponents(createCaptionLabel(caption), amountField, slider, maxAmountLabel);
        
        slider.addValueChangeListener(v -> loanAmountChanged(v.getValue().longValue()));
    }
    
    void setLoanValue(long loanValue) {
        slider.setLoanValue(loanValue);
    }
    
    long getLoanValue() {
        return slider.getValue().longValue();
    }
    
    void setMaxLoanValue(long maxLoanValue) {
        slider.setMax((double)maxLoanValue);
        maxAmountLabel.setValue("Max " + maxLoanValue + " million Ft" );
    }
    
    void setMinLoanValue(long minLoanValue) {
        this.minLoanValue = minLoanValue;
        if(slider.getValue() < minLoanValue) {
            setLoanValue(minLoanValue);
        }
    }
    
    private AmountField createAmountField() {
        AmountField amountField = new AmountField(null);
        amountField.setWidth("50");
        amountField.removeStyleName(ValoTheme.TEXTFIELD_SMALL);
        amountField.addValueChangeListener(event -> {
            try {
                loanAmountChanged(Long.parseLong(event.getValue()));
            } catch(NumberFormatException ex) {
                loanAmountChanged(0L);
            }
        });
        return amountField;
    }
    
    private MySlider createSlider() {
        MySlider slider = new MySlider();
        slider.setOrientation(SliderOrientation.HORIZONTAL);
        slider.setWidth("500px");
        return slider;
    }
    
    private Label createCaptionLabel(String caption) {
        Label label = new Label(caption);
        label.setWidth("120px");
        return label;
    }
    
    private Label createMaxAmountLabel() {
        Label label = new Label("Max 0 million Ft");
        //label.setWidth("130px");
        return label;
    }

    public void loanAmountChanged(long loanAmount) {
        long effectiveLoanAmount = effectiveLoanAmount(loanAmount);
        amountField.setAmount(effectiveLoanAmount);
        slider.setValue((double)effectiveLoanAmount);
        loanValueChangeListeners.stream().forEach(listener -> listener.loanValueChanged(effectiveLoanAmount));
    }
    
    private long effectiveLoanAmount(long loanAmount) {
        if(loanAmount < minLoanValue) {
            return minLoanValue;
        }
        if(loanAmount > slider.getMax()) {
            return (long)slider.getMax();
        }
        return loanAmount;
    }
    
    void addLoanValueChangeListener(LoanValueChangeListener loanValueChangeListener) {
        loanValueChangeListeners.add(loanValueChangeListener);
    }
    
    static interface LoanValueChangeListener {
        void loanValueChanged(long loanValue);
    }
    
    private static class MySlider extends Slider {
        
        void setLoanValue(long loanValue) {
            setValue((double)loanValue);
        }
    }
    
}
