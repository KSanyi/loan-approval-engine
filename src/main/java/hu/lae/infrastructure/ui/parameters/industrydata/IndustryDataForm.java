package hu.lae.infrastructure.ui.parameters.industrydata;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;

import hu.lae.domain.industry.Industry;
import hu.lae.domain.industry.IndustryData;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class IndustryDataForm extends CustomField<IndustryData>{

	private Map<Industry, PercentField> fields = new HashMap<>();
	
	public IndustryDataForm(IndustryData industryData) {
		for(Industry industry : Industry.values()) {
			PercentField percentField = new PercentField(industry.displayName);
			percentField.setPercent(industryData.ownEquityRatioAverage(industry));
            fields.put(industry, percentField);
        }
	}

	@Override
	public IndustryData getValue() {
		
		Map<Industry, Double> ownEquityRatioAverageMap = new HashMap<>();
		for(Industry industry : Industry.values()) {
			ownEquityRatioAverageMap.put(industry, fields.get(industry).getPercent());
		}
		return new IndustryData(ownEquityRatioAverageMap);
	}

	@Override
	protected Component initContent() {
		FormLayout layout = new FormLayout();
		fields.values().stream().forEach(layout::addComponent);
		layout.setMargin(true);
		Panel panel = new Panel("Own Equity Ratio Average", layout);
        panel.addStyleName("colored");
        panel.setSizeUndefined();
        return panel;
	}

	@Override
	protected void doSetValue(IndustryData value) {
		throw new IllegalStateException();
	}

}
