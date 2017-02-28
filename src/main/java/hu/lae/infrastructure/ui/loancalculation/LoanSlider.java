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

    private final Slider slider;
    
    private final Label maxAmountLabel = createMaxAmountLabel();
    
    private final AmountField amountField = createAmountField();
    
    private final List<LoanValueChangeListener> loanValueChangeListeners = new ArrayList<>();
    
    LoanSlider(String caption) {
        slider = createSlider();
        setSpacing(true);
        
        addComponents(createCaptionLabel(caption), amountField, slider, maxAmountLabel);
        
        slider.addValueChangeListener(v -> loanAmountChanged(((Double)v.getProperty().getValue()).longValue()));
    }
    
    void setLoanValue(long loanValue) {
        slider.setValue((double)loanValue);
    }
    
    long getLoanValue() {
        return slider.getValue().longValue();
    }
    
    void setMaxLoanValue(long maxLoanValue) {
        slider.setMax((double)maxLoanValue);
        maxAmountLabel.setValue("Max " + maxLoanValue + " millió Ft" );
    }
    
    private AmountField createAmountField() {
        AmountField amountField = new AmountField(null);
        amountField.setWidth("50");
        amountField.removeStyleName(ValoTheme.TEXTFIELD_SMALL);
        amountField.addTextChangeListener(event -> loanAmountChanged(Long.parseLong(event.getText())));
        return amountField;
    }
    
    private Slider createSlider() {
        Slider slider = new Slider();
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
        Label label = new Label("Max 0 millió Ft");
        label.setWidth("120px");
        return label;
    }

    public void loanAmountChanged(Long loanAmount) {
        long effectiveLoanAmount = loanAmount > slider.getMax() ? (long)slider.getMax() : loanAmount;
        amountField.setAmount(effectiveLoanAmount);
        slider.setValue((double)effectiveLoanAmount);
        loanValueChangeListeners.stream().forEach(listener -> listener.loanValueChanged(effectiveLoanAmount));
    }
    
    void addLoanValueChangeListener(LoanValueChangeListener loanValueChangeListener) {
        loanValueChangeListeners.add(loanValueChangeListener);
    }
    
    static interface LoanValueChangeListener {
        void loanValueChanged(long loanValue);
    }
    
}
