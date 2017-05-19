package hu.lae.infrastructure.ui;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.DateField;
import hu.lae.infrastructure.ui.component.ErrorSubmissionWindow;
import hu.lae.usermanagement.UserInfo;
import hu.lae.util.Clock;

@SuppressWarnings("serial")
public class Header extends HorizontalLayout {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Button errorButton = new Button("Send error", click -> postError());
	
	private final Button logoutButton = new Button("Logout", click -> logout());

	private final UserInfo userInfo;

	public Header(UserInfo userInfo) {
	    
		this.userInfo = userInfo;
		setSizeFull();
		setHeight("150px");
		setStyleName("dark");

		errorButton.addStyleName(ValoTheme.BUTTON_SMALL);
		errorButton.addStyleName(ValoTheme.BUTTON_DANGER);
		
		DateField currentDateField = new DateField("Current date");
        currentDateField.setValue(Clock.date());
        currentDateField.addStyleName("colored");
        currentDateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        currentDateField.setWidth("120px");
        currentDateField.addValueChangeListener(e -> Clock.setStaticDate(e.getValue()));
        
		Layout logoLayout = createLogoLayout();
		Layout userInfoLayout = createUserInfoLayout();
		addComponents(logoLayout, currentDateField, userInfoLayout);
		setComponentAlignment(userInfoLayout, Alignment.TOP_RIGHT);
		setComponentAlignment(currentDateField, Alignment.MIDDLE_CENTER);
	}

    private Layout createLogoLayout() {
		Label logo = new Label("Loan Application Engine");
		logo.setStyleName("mainLogo");
		logo.setSizeUndefined();
		
		Label subLogo = new Label("by Kocso IT Solutions KFT");
		subLogo.setStyleName("subLogo");
		subLogo.setSizeUndefined();
		
		VerticalLayout layout = new VerticalLayout(logo, subLogo);
		layout.setSizeUndefined();
		
		return layout;
	}

	private Layout createUserInfoLayout() {

		Label userLabel = new Label(userInfo.name);
		userLabel.addStyleName(ValoTheme.LABEL_COLORED);

		logoutButton.setStyleName(ValoTheme.BUTTON_SMALL);
		logoutButton.setIcon(VaadinIcons.USER);
		
		HorizontalLayout userInfoLayout = new HorizontalLayout(logoutButton, userLabel);

		VerticalLayout layout = new VerticalLayout(userInfoLayout, errorButton);
		layout.setSizeUndefined();
		
		return layout;
	}

	public void logout() {
		logger.info("User " + userInfo.loginName + " logged out");
		UI.getCurrent().getPage().setLocation("/");
		UI.getCurrent().getSession().close(); 
	}
	
	private void postError() {
        UI.getCurrent().addWindow(new ErrorSubmissionWindow());
    }
	
}

