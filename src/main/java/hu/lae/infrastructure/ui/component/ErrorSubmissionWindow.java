package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class ErrorSubmissionWindow extends Window {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final TextArea textArea = new TextArea();
    
    private final Button submitButton = new Button("Submit error", click -> submit());
    
    public ErrorSubmissionWindow() {
        
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addStyleName(ValoTheme.BUTTON_DANGER);
        submitButton.setClickShortcut(KeyCode.ENTER);
        
        textArea.setSizeFull();
        textArea.addStyleName(ValoTheme.TEXTAREA_SMALL);
        
        VerticalLayout layout = new VerticalLayout(textArea, submitButton);
        layout.setComponentAlignment(submitButton, Alignment.BOTTOM_CENTER);
        layout.setSizeFull();
        layout.setExpandRatio(textArea, 1);
        
        setContent(layout);
        setWidth("400px");
        setHeight("300px");
        setCaption("Error submission");
        setModal(true);
    }

    private void submit() {
        String errorMessage = textArea.getValue();
        if(errorMessage == null || errorMessage.isEmpty()) {
            Notification.show("Please leave some message to help me debugging", Type.WARNING_MESSAGE);
        } else {
            logger.error("Error was posted by {}: {}", LaeUI.currentUser(), textArea.getValue());
            close();
        }
    }
    
}
