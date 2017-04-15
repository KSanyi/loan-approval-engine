package hu.lae.infrastructure.ui;

import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.usermanagement.Authenticator;
import hu.lae.usermanagement.Authenticator.AuthenticationException;
import hu.lae.usermanagement.Authenticator.WrongPasswordException;
import hu.lae.usermanagement.UserInfo;

@SuppressWarnings("serial")
class LoginWindow extends Window {

    private final TextField userNameField = new TextField("User name:");
    private final PasswordField passwordField = new PasswordField("Password:");
    private final Button button = new Button("Log in", click -> logIn());
    
    private final Authenticator authenticator;
    
    private final Consumer<UserInfo> successFulLoginHandler;
    
    LoginWindow(Authenticator authenticator, Consumer<UserInfo> successFulLoginHandler) {
        this.authenticator = authenticator;
        this.successFulLoginHandler = successFulLoginHandler;
        
        setCaption("Loan Application Engine");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setDraggable(false);
        setWidth("300px");
        userNameField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        passwordField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        FormLayout layout = new FormLayout(userNameField, passwordField);
        layout.setMargin(true);
        setContent(layout);
        
        layout.addComponent(button);
        layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
    }

    private void logIn() {
        try {
            UserInfo userInfo = authenticator.authenticateUser(userNameField.getValue(), passwordField.getValue());
            successFulLoginHandler.accept(userInfo);
            close();
        } catch (WrongPasswordException ex) {
            Notification.show("Login failed", "Wrong user name or password", Notification.Type.ERROR_MESSAGE);
        } catch (AuthenticationException ex) {
            Notification.show("Login failed", "Error durong authentication", Notification.Type.ERROR_MESSAGE);
        }
    }
    
}
