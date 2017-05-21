package hu.lae.infrastructure.ui.riskparameters;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.ComboBox;
import hu.lae.riskparameters.Industry;
import hu.lae.riskparameters.MaxLoanDurations;

@SuppressWarnings("serial")
public class MaxLoanDurationsForm extends FormLayout {

    private final Map<Industry, ComboBox<Integer>> combos = new LinkedHashMap<>();
    
    MaxLoanDurationsForm(MaxLoanDurations maxLoanDurations) {
    
        setCaption("Max loan durations");
        
        setSpacing(false);
        setMargin(false);
        
        for(Industry industry : Industry.values()) {
            ComboBox<Integer> combo = new ComboBox<>(industry.displayName, generateComboValues());
            combo.setValue(maxLoanDurations.maxLoanDuration(industry));
            combo.addStyleName(ValoTheme.COMBOBOX_SMALL);
            combo.setEmptySelectionAllowed(false);
            combo.setWidth("60px");
            combos.put(industry, combo);
            addComponent(combo);
            setComponentAlignment(combo, Alignment.MIDDLE_LEFT);
        }
    }
    
    public MaxLoanDurations getMaxLoanDurations() {
        Map<Industry, Integer> map = new HashMap<>();
        for(Industry industry : Industry.values()) {
            map.put(industry, combos.get(industry).getValue());
        }
        
        return new MaxLoanDurations(map);
    }
    
    private static List<Integer> generateComboValues() {
        return IntStream.range(1, 31).mapToObj(i -> i).collect(Collectors.toList());
    }
    
}
