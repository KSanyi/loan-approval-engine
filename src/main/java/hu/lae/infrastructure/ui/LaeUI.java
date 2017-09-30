package hu.lae.infrastructure.ui;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.industry.IndustryData;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.infrastructure.server.ApplicationService;
import hu.lae.infrastructure.server.LaeServlet;
import hu.lae.infrastructure.ui.client.ClientPanel;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.loancalculation.proposal.ProposalWindow;
import hu.lae.infrastructure.ui.parameters.ParametersWindow;
import hu.lae.usermanagement.UserInfo;
import hu.lae.util.Clock;

@Theme("lae")
public class LaeUI extends UI {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);
	
	private ApplicationService applicationService = ((LaeServlet)VaadinServlet.getCurrent()).applicationService;
	
	private UserInfo userInfo;

	private VerticalLayout mainScreenHolder = new VerticalLayout();
	
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
	    
	    VerticalLayout pageLayout = new VerticalLayout(new Header(userInfo), new Menu(), mainScreenHolder);
	    pageLayout.setMargin(false);
	    pageLayout.setSpacing(false);
	    pageLayout.setSizeUndefined();
	    
		setContent(pageLayout);
		
		setScreen(createClientScreen());
	}
	
	public static LaeUI getCurrent() {
		return (LaeUI)UI.getCurrent();
	}

    public void showRiskParametersScreen() {
        ParametersWindow.show(applicationService.riskParameterRepository, applicationService.industryDataRepository);
    }
    
    private void setScreen(Layout screen) {
        mainScreenHolder.removeAllComponents();
        mainScreenHolder.addComponent(screen);
    }
    
    private Layout createClientScreen() {
        ClientPanel clientPanel = new ClientPanel(Client.createDefault());
        
        Button calculateButton = new Button("Calculate");
        calculateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        calculateButton.addStyleName(ValoTheme.BUTTON_LARGE);
        
        calculateButton.addClickListener(click -> {
            RiskParameters riskParameters = applicationService.riskParameterRepository.loadRiskParameters();
            IndustryData industryData = applicationService.industryDataRepository.loadIndustryData();
            LocalDate currentDate = Clock.date();
            
            Client client = clientPanel.getClient();
            if(!client.existingLoans.isValid(Clock.date())) {
                Notification.show("Validation error", "Change the expiry of existing loans", Notification.Type.ERROR_MESSAGE);
            } else {
                logger.info("Risk Parameters: " + riskParameters);
                logger.info("Client: " + client);
                logger.info("Date: " + currentDate);
                ProposalWindow calculatorWindow = new ProposalWindow(new LoanCalculator(riskParameters, industryData, currentDate), client, currentDate);
                UI.getCurrent().addWindow(calculatorWindow);                
            }
        });
        
        VerticalLayout layout = new VerticalLayout(clientPanel, calculateButton);
        layout.setMargin(false);
        layout.setComponentAlignment(calculateButton, Alignment.BOTTOM_CENTER);
        
        return layout;
    }
	
}