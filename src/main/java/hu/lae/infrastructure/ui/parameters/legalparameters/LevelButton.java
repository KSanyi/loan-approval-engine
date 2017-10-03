package hu.lae.infrastructure.ui.parameters.legalparameters;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalIssueType.Level;

@SuppressWarnings("serial")
public class LevelButton extends CustomField<Level>{

    private Level level = Level.GO;
    
    private final Button button = new Button();
    
    LevelButton(Level level) {
        this.level = level;
        initButton(level);
        button.setHeight("25px");
        button.addClickListener(click -> clicked());
    }
    
    private void clicked() {
        int index = level.ordinal();
        level = Level.values()[(index+1) % Level.values().length];
        initButton(level);
    }
    
    private void initButton(Level level) {
        button.setCaption(level.name());
        switch(level) {
        case GO: button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
            break;
        case JUDGE: button.setStyleName(ValoTheme.BUTTON_PRIMARY);
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
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        return button;
    }

    @Override
    protected void doSetValue(Level value) {
        throw new IllegalStateException();
    }

}
