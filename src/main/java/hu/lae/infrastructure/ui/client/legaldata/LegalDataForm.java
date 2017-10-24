package hu.lae.infrastructure.ui.client.legaldata;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalData;
import hu.lae.infrastructure.ui.component.Button;

@SuppressWarnings("serial")
class LegalDataForm extends CustomField<LegalData> {

	private final LegalIssuesTable legalIssuesTable;
	
	private final Button addLegalIssueButton = new Button("Add legal issue", click -> addLegalIssue());
	
	LegalDataForm(LegalData legalData) {
		legalIssuesTable = new LegalIssuesTable(legalData.legalIssues);
	}

	private void addLegalIssue() {
		LegalIssueWindow.open(issue -> legalIssuesTable.addIssue(issue));
	}

	public LegalData getValue() {
		return new LegalData(legalIssuesTable.getLegalIssues());
	}

	@Override
	protected Component initContent() {
		VerticalLayout layout = new VerticalLayout(legalIssuesTable, addLegalIssueButton);
		layout.setMargin(false);
		
		addLegalIssueButton.addStyleName(ValoTheme.BUTTON_SMALL);
		addLegalIssueButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		layout.setComponentAlignment(addLegalIssueButton, Alignment.BOTTOM_CENTER);
		return layout;
	}

	@Override
    protected void doSetValue(LegalData value) {
        throw new UnsupportedOperationException();
    }

}
