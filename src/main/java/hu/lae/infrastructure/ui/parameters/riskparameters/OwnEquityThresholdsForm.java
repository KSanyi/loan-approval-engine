package hu.lae.infrastructure.ui.parameters.riskparameters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.shared.ui.MarginInfo;
import hu.lae.infrastructure.ui.component.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.riskparameters.OwnEquityRatioThresholds;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class OwnEquityThresholdsForm extends CustomField<OwnEquityRatioThresholds> {

	private final PercentField threshold1Field = new PercentField("Threshold 1");
	private final ComboBox<Integer> yearForBelowThreshold1Combo = new ComboBox<>("Years 1", generateComboValues(5));
	private final PercentField threshold2Field = new PercentField("Threshold 2");
	private final ComboBox<Integer> yearForBelowThreshold2Combo = new ComboBox<>("Years 2", generateComboValues(5));
	
	OwnEquityThresholdsForm(OwnEquityRatioThresholds ownEquityRatioThresholds) {
		
		threshold1Field.setPercent(ownEquityRatioThresholds.threshold1);
		threshold2Field.setPercent(ownEquityRatioThresholds.threshold2);
		
		yearForBelowThreshold1Combo.setValue(ownEquityRatioThresholds.yearForBelowThreshold1);
		yearForBelowThreshold2Combo.setValue(ownEquityRatioThresholds.yearForBelowThreshold2);
	}
	
	@Override
	public OwnEquityRatioThresholds getValue() {
		return new OwnEquityRatioThresholds(threshold1Field.getPercent(), yearForBelowThreshold1Combo.getValue(), threshold2Field.getPercent(), yearForBelowThreshold2Combo.getValue());
	}

	@Override
	protected Component initContent() {
	    HorizontalLayout layout = new HorizontalLayout();
	    layout.setMargin(new MarginInfo(false, true));
        
        threshold1Field.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        threshold2Field.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        
        yearForBelowThreshold1Combo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        yearForBelowThreshold2Combo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        
        yearForBelowThreshold1Combo.setWidth("60px");
        yearForBelowThreshold2Combo.setWidth("60px");
        
        yearForBelowThreshold1Combo.setEmptySelectionAllowed(false);
        yearForBelowThreshold2Combo.setEmptySelectionAllowed(false);
        
        FormLayout formLayout1 = new FormLayout(threshold1Field, threshold2Field);
        formLayout1.setMargin(false);
        
        FormLayout formLayout2 = new FormLayout(yearForBelowThreshold1Combo, yearForBelowThreshold2Combo);
        formLayout2.setMargin(false);
        
        layout.addComponents(formLayout1, formLayout2);
        
        Panel panel = new Panel("Own equity ratio thresholds", layout);
        panel.addStyleName("colored");
		return panel;
	}

	@Override
	protected void doSetValue(OwnEquityRatioThresholds value) {
		throw new IllegalStateException();		
	}
	
	private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }

}
