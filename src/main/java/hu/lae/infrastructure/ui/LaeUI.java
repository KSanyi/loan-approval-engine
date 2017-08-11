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

import hu.lae.domain.Client;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.infrastructure.server.ApplicationService;
import hu.lae.infrastructure.server.LaeServlet;
import hu.lae.infrastructure.ui.client.ClientPanel;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.loancalculation.proposal.ProposalWindow;
import hu.lae.infrastructure.ui.riskparameters.RiskParametersPanel;
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
	    
	    RiskParametersPanel riskParametersPanel = new RiskParametersPanel(applicationService.riskParameterRepository);
	    
	    ClientPanel clientPanel = new ClientPanel(Client.createDefault());
	    
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
	            logger.info("Risk Parameters: " + riskParameters);
	            logger.info("Client: " + client);
	            logger.info("Date: " + currentDate);
	            ProposalWindow calculatorWindow = new ProposalWindow(new LoanCalculator(riskParameters, currentDate), client, currentDate);
	            UI.getCurrent().addWindow(calculatorWindow);	            
	        }
	    });
	    
	    VerticalLayout rightLayout = new VerticalLayout(clientPanel, calculateButton);
	    rightLayout.setMargin(false);
	    rightLayout.setComponentAlignment(calculateButton, Alignment.BOTTOM_CENTER);
        HorizontalLayout mainLayout = new HorizontalLayout(riskParametersPanel, rightLayout);
        mainLayout.setMargin(true);
	    
	    VerticalLayout pageLayout = new VerticalLayout(new Header(userInfo), mainLayout);
	    pageLayout.setMargin(false);
	    pageLayout.setSpacing(false);
	    pageLayout.setSizeUndefined();
	    
		setContent(pageLayout);
	}
	
	public static LaeUI getCurrent() {
		return (LaeUI)UI.getCurrent();
	}
	
}