package hu.lae.infrastructure.ui.parameters.riskparameters;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.riskparameters.OwnEquityRatioThresholds;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class OwnEquityThresholdsForm extends CustomField<OwnEquityRatioThresholds> {

	private final PercentField threshold1Field = new PercentField("Threshold 1");
	private final PercentField threshold2Field = new PercentField("Threshold 2");
	
	OwnEquityThresholdsForm(OwnEquityRatioThresholds ownEquityRatioThresholds) {
		
		threshold1Field.setPercent(ownEquityRatioThresholds.threshold1);
		threshold2Field.setPercent(ownEquityRatioThresholds.threshold2);
	}
	
	@Override
	public OwnEquityRatioThresholds getValue() {
		return new OwnEquityRatioThresholds(threshold1Field.getPercent(), threshold2Field.getPercent());
	}

	@Override
	protected Component initContent() {
		FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(false);
        formLayout.setMargin(new MarginInfo(false, true));
        
        threshold1Field.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        threshold2Field.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        
        formLayout.addComponents(threshold1Field, threshold2Field);
        formLayout.setComponentAlignment(threshold1Field, Alignment.MIDDLE_LEFT);
        formLayout.setComponentAlignment(threshold2Field, Alignment.MIDDLE_LEFT);
        
        Panel panel = new Panel("Own equity ratio thresholds", formLayout);
        panel.addStyleName("colored");
		return panel;
	}

	@Override
	protected void doSetValue(OwnEquityRatioThresholds value) {
		throw new IllegalStateException();		
	}

}
