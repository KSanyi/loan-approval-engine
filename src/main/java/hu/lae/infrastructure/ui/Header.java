package hu.lae.infrastructure.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.usermanagement.UserInfo;

public class Header extends HorizontalLayout implements Button.ClickListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(Header.class);

	private final Button logoutButton = new Button("Logout", this);

	private final UserInfo userInfo;

	public Header(UserInfo userInfo) {
	    
		this.userInfo = userInfo;
		setSizeFull();
		setHeight("135px");
		setSpacing(true);
		setMargin(true);
		setStyleName("dark");

		Layout logoLayout = createLogoLayout();
		Layout userInfoLayout = createUserInfoLayout();
		addComponents(logoLayout, userInfoLayout);
		setComponentAlignment(userInfoLayout, Alignment.TOP_RIGHT);
	}

	private Layout createLogoLayout() {
		Label logo = new Label("Loan Application Engine");
		logo.setStyleName("mainLogo");
		
		Label subLogo = new Label("by Kocso IT Solutions KFT");
		subLogo.setStyleName("subLogo");
		
		return new VerticalLayout(logo, subLogo);
	}

	private Layout createUserInfoLayout() {
		Embedded userPic = new Embedded(null, new ThemeResource("../runo/icons/16/user.png"));
		userPic.setSizeUndefined();

		Label userLabel = new Label(userInfo.name);
		userLabel.addStyleName(ValoTheme.LABEL_COLORED);

		logoutButton.setStyleName(ValoTheme.BUTTON_SMALL);
		
		HorizontalLayout userInfoLayout = new HorizontalLayout(logoutButton, userPic, userLabel);
        userInfoLayout.setSpacing(true);

		return userInfoLayout;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == logoutButton) {
			logger.info("User " + userInfo.loginName + " logged out");
			UI.getCurrent().getPage().setLocation("/");
			UI.getCurrent().getSession().close();
		} 
	}
}

