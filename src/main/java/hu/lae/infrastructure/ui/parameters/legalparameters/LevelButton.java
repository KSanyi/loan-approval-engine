package hu.lae.infrastructure.ui.parameters.legalparameters;

import hu.lae.infrastructure.ui.component.Button;

import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalIssueType.Level;

@SuppressWarnings("serial")
public class LevelButton extends CustomField<Level>{

    private Level level = Level.GO;
    
    private List<Level> availableLevels;
    
    private final Button button;
    
    LevelButton(Level level, List<Level> availableLevels, String id) {

    	if(!availableLevels.contains(level)) throw new IllegalArgumentException("Available levels " + availableLevels + " do not contain level " + level);
    	
        this.availableLevels = availableLevels;
        this.button = new Button("", id);
        this.level = level;
        refershButton();
        button.setHeight("25px");
        button.addClickListener(click -> clicked());
    }
    
    private void clicked() {
        int index = availableLevels.indexOf(level);
        Level level = availableLevels.get((index+1) % availableLevels.size());
        setValue(level);
    }
    
    private void refershButton() {
        button.setCaption(level.name());
        switch(level) {
        case GO: button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
            break;
        case JUDGE: button.setStyleName("warning");
            break;
        case NOGO: button.setStyleName(ValoTheme.BUTTON_DANGER);
            break;
        }
        button.setWidth("80px");
    }
    
    @Override
    public Level getValue() {
        return level;
    }

    @Override
    protected Component initContent() {
        return button;
    }

    @Override
    protected void doSetValue(Level level) {
        this.level = level;
        refershButton();
    }

	public void setAvailableLevels(List<Level> availableLevels) {
		this.availableLevels = availableLevels;
	}

}
