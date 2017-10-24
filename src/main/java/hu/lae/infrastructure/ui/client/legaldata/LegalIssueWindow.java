package hu.lae.infrastructure.ui.client.legaldata;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalData.Entity;
import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.component.ComboBox;
import hu.lae.infrastructure.ui.component.DateField;

@SuppressWarnings("serial")
class LegalIssueWindow extends Window {

	private final Consumer<LegalIssue> action;
	
	private final LegalIssueForm legalIssueForm = new LegalIssueForm();
	
	private final Button saveButton = new Button("Save", click -> save());
	
	private LegalIssueWindow(Consumer<LegalIssue> action) {
		setCaption("New Legal Issue");
		this.action = action;
		saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
		
		VerticalLayout layout = new VerticalLayout(legalIssueForm, saveButton);
		layout.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
		setContent(layout);
		setModal(true);
	}
	
	private void save() {
		action.accept(legalIssueForm.getValue());
		close();
	}
	
	static void open(Consumer<LegalIssue> action) {
		UI.getCurrent().addWindow(new LegalIssueWindow(action));
	}
	
}

@SuppressWarnings("serial")
class LegalIssueForm extends CustomField<LegalIssue> {

	private final ComboBox<LegalIssueType> typeCombo = new ComboBox<>("Type", Arrays.asList(LegalIssueType.values()));
	
	private final CheckBox inProgressCheck = new CheckBox("In progress", true);
	
	private final DateField dateField = new DateField("Date");
	
	private final RadioButtonGroup<Entity> entityRadio = new RadioButtonGroup<>("Entity", Arrays.asList(Entity.values()));
	
	@Override
	public LegalIssue getValue() {
		return new LegalIssue(typeCombo.getValue(),
				inProgressCheck.getValue() ? Optional.empty() : Optional.of(dateField.getValue()),
				entityRadio.getValue());
	}

	@Override
	protected Component initContent() {
		typeCombo.setEmptySelectionAllowed(false);
		typeCombo.setWidth("300px");
		typeCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
		typeCombo.setItemCaptionGenerator(type -> type.displayName);
		typeCombo.setValue(LegalIssueType.values()[0]);
		
		inProgressCheck.addStyleName(ValoTheme.CHECKBOX_SMALL);
		inProgressCheck.addValueChangeListener(event -> dateField.setEnabled(!event.getValue()));
		
		dateField.setEnabled(false);
		dateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
		dateField.setWidth("110px");
		
		entityRadio.setValue(Entity.COMPANY);
		entityRadio.setItemCaptionGenerator(entity -> entity.displayName);
		
		HorizontalLayout dateLayput = new HorizontalLayout(inProgressCheck, dateField);
		dateLayput.setComponentAlignment(inProgressCheck, Alignment.MIDDLE_CENTER);
		VerticalLayout layout = new VerticalLayout(typeCombo, dateLayput, entityRadio);
		layout.setMargin(false);
		
		return layout;
	}

	@Override
	protected void doSetValue(LegalIssue value) {
		throw new UnsupportedOperationException(); 
	}
	
}
