package hu.lae.infrastructure.ui.parameters.riskparameters;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.industry.Industry;
import hu.lae.domain.riskparameters.IndustryMaxLoanDurations;
import hu.lae.infrastructure.ui.component.ComboBox;

@SuppressWarnings("serial")
class IndustryMaxLoanDurationsForm extends CustomField<IndustryMaxLoanDurations> {

    private final Map<Industry, ComboBox<Integer>> combos = new LinkedHashMap<>();
    
    IndustryMaxLoanDurationsForm(IndustryMaxLoanDurations maxLoanDurations) {
    
        for(Industry industry : Industry.values()) {
            ComboBox<Integer> combo = new ComboBox<>(industry.displayName, generateComboValues());
            combo.setValue(maxLoanDurations.maxLoanDuration(industry));
            combos.put(industry, combo);
        }
    }
    
    @Override
    public IndustryMaxLoanDurations getValue() {
        Map<Industry, Integer> map = new HashMap<>();
        for(Industry industry : Industry.values()) {
            map.put(industry, combos.get(industry).getValue());
        }
        
        return new IndustryMaxLoanDurations(map);
    }

    @Override
    protected Component initContent() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(false);
        formLayout.setMargin(new MarginInfo(false, true));
        
        for(Industry industry : Industry.values()) {
            ComboBox<Integer> combo = combos.get(industry);
            combo.addStyleName(ValoTheme.COMBOBOX_SMALL);
            combo.setEmptySelectionAllowed(false);
            combo.setWidth("60px");
            
            formLayout.addComponent(combo);
            formLayout.setComponentAlignment(combo, Alignment.MIDDLE_LEFT);
        }
        
        Panel panel = new Panel("Max loan durations", formLayout);
        panel.addStyleName("colored");
        
        return panel;
    }
    
    private static List<Integer> generateComboValues() {
        return IntStream.range(1, 31).mapToObj(i -> i).collect(Collectors.toList());
    }

    @Override
    protected void doSetValue(IndustryMaxLoanDurations value) {
        throw new IllegalStateException();
    }
    
}
