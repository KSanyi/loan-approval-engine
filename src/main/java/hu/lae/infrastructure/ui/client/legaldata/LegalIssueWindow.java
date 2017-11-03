package hu.lae.infrastructure.ui.client.legaldata;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalData.Entity;
import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.component.CheckBox;
import hu.lae.infrastructure.ui.component.ComboBox;
import hu.lae.infrastructure.ui.component.DateField;
import hu.lae.infrastructure.ui.component.RadioButtonGroup;
import hu.lae.infrastructure.ui.component.Window;
import hu.lae.util.Clock;

@SuppressWarnings("serial")
class LegalIssueWindow extends Window {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
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
	    LegalIssue legalIssue = legalIssueForm.getValue();
		action.accept(legalIssue);
		logger.debug("LegalIssue added: {}", legalIssue);
		close();
	}
	
	static void open(Consumer<LegalIssue> action) {
		UI.getCurrent().addWindow(new LegalIssueWindow(action));
	}
	
}

@SuppressWarnings("serial")
class LegalIssueForm extends CustomField<LegalIssue> {

	private final ComboBox<LegalIssueType> typeCombo = new ComboBox<>("Type", Arrays.asList(LegalIssueType.values()));
	
	private final CheckBox inProgressCheck = new CheckBox("In progress");
	
	private final DateField dateField = new DateField("Date closed");
	
	private final RadioButtonGroup<Entity> entityRadio = new RadioButtonGroup<>("Entity", Arrays.asList(Entity.values()));
	
	private final AmountField valueField = new AmountField("Value");
	
	@Override
	public LegalIssue getValue() {
		
		LegalIssueType type = typeCombo.getValue();
		
		Optional<Integer> amount = type.hasMaterialityThreshold ? Optional.of((int)valueField.getAmount()) : Optional.empty();
		
		return new LegalIssue(type,
				inProgressCheck.getValue() ? Optional.empty() : Optional.of(dateField.getValue()),
				entityRadio.getValue(),
				amount);
	}

	@Override
	protected Component initContent() {
		typeCombo.setEmptySelectionAllowed(false);
		typeCombo.setWidth("300px");
		typeCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
		typeCombo.setItemCaptionGenerator(type -> type.displayName);
		typeCombo.setValue(LegalIssueType.values()[0]);
		typeCombo.addValueChangeListener(event -> valueField.setVisible(event.getValue().hasMaterialityThreshold));
		
		inProgressCheck.addStyleName(ValoTheme.CHECKBOX_SMALL);
		inProgressCheck.setValue(true);
		inProgressCheck.addValueChangeListener(event -> dateField.setEnabled(!event.getValue()));
		
		dateField.setRangeEnd(Clock.date());
		dateField.setEnabled(false);
		dateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
		dateField.setWidth("110px");
		
		entityRadio.setValue(Entity.COMPANY);
		entityRadio.setItemCaptionGenerator(entity -> entity.displayName);
		
		valueField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
		valueField.setVisible(typeCombo.getValue().hasMaterialityThreshold);
		
		HorizontalLayout dateLayput = new HorizontalLayout(inProgressCheck, dateField);
		dateLayput.setComponentAlignment(inProgressCheck, Alignment.MIDDLE_CENTER);
		VerticalLayout layout = new VerticalLayout(typeCombo, dateLayput, entityRadio, valueField);
		layout.setMargin(false);
		
		return layout;
	}

	@Override
	protected void doSetValue(LegalIssue value) {
		throw new UnsupportedOperationException(); 
	}
	
}
