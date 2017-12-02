package hu.lae.infrastructure.ui.loancalculation.proposal;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.finance.ebitdacorrection.CorrectedEbitdas;

@SuppressWarnings("serial")
class EbitdaCheckWindow extends Window {

	EbitdaCheckWindow(CorrectedEbitdas correctedEbitdas) {
		setCaption("EBITDA check");
		setModal(true);
		
		GridLayout layout = new GridLayout();
		layout.setColumns(4);
		layout.setRows(correctedEbitdas.calculationLog.subResultsMap.size()+1);
		layout.setMargin(true);
		layout.setSpacing(true);
		
		layout.addComponent(new Label("T"), 1, 0);
		layout.addComponent(new Label("T-1"), 2, 0);
		layout.addComponent(new Label("T-2"), 3, 0);
		int i=1;
		for(String key : correctedEbitdas.calculationLog.subResultsMap.keySet()) {
			Label label = new Label(key);
			layout.addComponent(label, 0, i);
			for(int j=0;j<correctedEbitdas.calculationLog.subResultsMap.get(key).size();j++) {
				TextField field = new TextField(null, String.valueOf(correctedEbitdas.calculationLog.subResultsMap.get(key).get(j)));
				field.addStyleName(ValoTheme.TEXTFIELD_SMALL);
				field.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
				field.setReadOnly(true);
				field.setWidth("70px");
				layout.addComponent(field, j+1, i);
			}
			i++;
		}
		
		setContent(layout);
		
		setWidth("600px");
	}

	static void show(CorrectedEbitdas correctedEbitdas) {
		UI.getCurrent().addWindow(new EbitdaCheckWindow(correctedEbitdas));
	}
	
}
