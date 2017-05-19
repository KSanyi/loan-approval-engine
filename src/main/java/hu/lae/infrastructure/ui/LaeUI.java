package hu.lae.infrastructure.ui;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.Client;
import hu.lae.infrastructure.server.ApplicationService;
import hu.lae.infrastructure.server.LaeServlet;
import hu.lae.infrastructure.ui.client.ClientPanel;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.component.DateField;
import hu.lae.infrastructure.ui.loancalculation.ProposalWindow;
import hu.lae.infrastructure.ui.riskparameters.RiskParametersPanel;
import hu.lae.loan.LoanCalculator;
import hu.lae.riskparameters.RiskParameters;
import hu.lae.usermanagement.UserInfo;
import hu.lae.util.Clock;

@Theme("lae")
public class LaeUI extends UI {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);
	
	private ApplicationService applicationService = ((LaeServlet)VaadinServlet.getCurrent()).applicationService;
	
	private UserInfo userInfo;

	@Override
	protected void init(VaadinRequest request) {

        logger.debug("LAE-UI initialized");
        
        if(userInfo == null) {
            showLogin();
        }
        
        addShortcutListener(VaadinUtil.createErrorSubmissionShortcutListener());
	}
	
	private void showLogin() {
	    LoginWindow loginWindow = new LoginWindow(applicationService.authenticator, this::buildUI);
	    addWindow(loginWindow);
	}
	
	private void buildUI(UserInfo userInfo) {
	    logger.info(userInfo.loginName + " logged in");
	    
	    DateField currentDateField = new DateField("Current date");
	    currentDateField.setValue(Clock.date());
	    currentDateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
	    currentDateField.setWidth("120px");
	    currentDateField.addValueChangeListener(e -> Clock.setStaticDate(e.getValue()));
	    
	    RiskParametersPanel riskParametersPanel = new RiskParametersPanel(applicationService.riskParameterRepository);
	    
	    ClientPanel clientPanel = new ClientPanel(Client.createDefault());
	    
	    HorizontalLayout mainLayout = new HorizontalLayout(riskParametersPanel, clientPanel);
	    mainLayout.setMargin(true);
	    
	    Button calculateButton = new Button("Calculate");
	    calculateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	    calculateButton.addStyleName(ValoTheme.BUTTON_LARGE);
	    
	    calculateButton.addClickListener(click -> {
	        RiskParameters riskParameters = riskParametersPanel.getRiskParameters();
	        LocalDate currentDate = Clock.date();
	        
	        Client client = clientPanel.getClient();
	        if(!client.existingLoans.isValid(Clock.date())) {
	            Notification.show("Validation error", "Change the expiry of existing loans", Notification.Type.ERROR_MESSAGE);
	        } else {
	            ProposalWindow calculatorWindow = new ProposalWindow(new LoanCalculator(riskParameters, currentDate), client, currentDate);
	            UI.getCurrent().addWindow(calculatorWindow);	            
	        }
	    });
	    
	    VerticalLayout pageLayout = new VerticalLayout(new Header(userInfo), currentDateField, mainLayout, calculateButton);
	    pageLayout.setMargin(false);
	    pageLayout.setSpacing(false);
	    pageLayout.setComponentAlignment(currentDateField, Alignment.MIDDLE_CENTER);
	    pageLayout.setComponentAlignment(calculateButton, Alignment.MIDDLE_CENTER);
		setContent(pageLayout);
	}
	
	public static LaeUI getCurrent() {
		return (LaeUI)UI.getCurrent();
	}
	
}