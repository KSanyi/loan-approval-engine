package hu.lae.infrastructure.ui.client.legaldata;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class LegalIssuesTable extends Grid<LegalIssue> {
	
	private final List<LegalIssue> legalIssues;
	
	LegalIssuesTable(List<LegalIssue> legalIssues) {
		
		this.legalIssues = new ArrayList<>(legalIssues);
		
		addColumn(l -> l.type.displayName)
        	.setCaption("Type");
		
		addColumn(l -> l.date.isPresent() ? Formatters.formatDate(l.date.get()) : "In progress")
    		.setCaption("Date closed").setWidth(120);
		
		addColumn(l -> l.entity.displayName)
			.setCaption("Entity").setWidth(150);
		
		addColumn(l -> l.value.isPresent() ? Formatters.formatAmount(l.value.get()) : "-")
			.setCaption("Value").setWidth(80);
		
		addColumn(l -> new DeleteButton(this, l), new ComponentRenderer())
			.setCaption("").setWidth(80);
		
		setItems(legalIssues);
		
		setSelectionMode(SelectionMode.NONE);
        
        getColumns().stream().forEach(column -> {
            column.setResizable(false);
            column.setSortable(false);
        });
        
        setHeightByRows(Math.max(1, legalIssues.size()));
        addStyleName(VaadinUtil.GRID_SMALL);
        
        setWidth("650px");
	}
	
	private void removeIssue(LegalIssue issue) {
		legalIssues.remove(issue);
		setItems(legalIssues);
		setHeightByRows(Math.max(1, legalIssues.size()));
	}
	
	void addIssue(LegalIssue issue) {
		legalIssues.add(issue);
		setItems(legalIssues);
		setHeightByRows(Math.max(1, legalIssues.size()));
	}
	
	List<LegalIssue> getLegalIssues() {
		return legalIssues;
	}
	
	private static class DeleteButton extends Button {
		
		public DeleteButton(LegalIssuesTable legalIssuesTable, LegalIssue legalIssue) {
			setCaption("X");
			addStyleName(ValoTheme.BUTTON_LINK);
			addClickListener(click -> legalIssuesTable.removeIssue(legalIssue));
		}
		
	}
	
}
