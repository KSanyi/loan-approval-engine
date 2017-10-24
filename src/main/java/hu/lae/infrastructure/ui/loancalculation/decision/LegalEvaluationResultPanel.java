package hu.lae.infrastructure.ui.loancalculation.decision;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.domain.legal.LegalEvaluationResult;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class LegalEvaluationResultPanel extends Panel {

	LegalEvaluationResultPanel(LegalEvaluationResult result) {
		
		setCaption("Legal evaluation result");
		
		Label label = new Label("Decision: " + result.value.name());
		label.addStyleName(ValoTheme.LABEL_H3);
		
		Grid<LegalIssue> issuesTable = new Grid<>();
		issuesTable.addColumn(i -> i.type.displayName).setCaption("Issue");
		issuesTable.addColumn(l -> l.date.isPresent() ? Formatters.formatDate(l.date.get()) : "In progress").setCaption("Date");
		issuesTable.addColumn(i -> i.entity.displayName).setCaption("Entity");
		issuesTable.addStyleName(VaadinUtil.GRID_SMALL);
		issuesTable.setItems(result.issues);
		issuesTable.setHeightByRows(result.issues.size());
		
		VerticalLayout layout = new VerticalLayout(label, issuesTable);
		setContent(layout);
	}
	
}
