package hu.lae.infrastructure.ui;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class Menu extends MenuBar {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    Menu() {
        setStyleName(ValoTheme.MENUBAR_SMALL);
        setSizeFull();
        
        MenuItem openingHoursMenuItem = addItem("Menu", null);
        openingHoursMenuItem.addItem("Parameters", new LoggerCommand(c -> LaeUI.getCurrent().showRiskParametersScreen()));
    }
    
    private static class LoggerCommand implements Command {
        private final Command action;
        
        public LoggerCommand(Command action){
            this.action = action;
        }
        
        @Override
        public void menuSelected(MenuItem selectedItem) {
            logger.debug("USERACTION: Menuitem '" + selectedItem.getText() + "' was clicked");
            action.menuSelected(selectedItem);
        }
    }
    
}

