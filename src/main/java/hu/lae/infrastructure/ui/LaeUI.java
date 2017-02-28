package hu.lae.infrastructure.ui;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.IncomeStatement;
import hu.lae.infrastructure.server.ApplicationService;
import hu.lae.infrastructure.server.LaeServlet;
import hu.lae.infrastructure.ui.balancesheet.BalanceSheetPanel;
import hu.lae.infrastructure.ui.incomestatement.IncomeStatementPanel;
import hu.lae.infrastructure.ui.loancalculation.CalculatorWindow;
import hu.lae.infrastructure.ui.riskparameters.RiskParametersPanel;
import hu.lae.loan.LoanCalculator;
import hu.lae.loan.MaxLoanDistributor;
import hu.lae.riskparameters.RiskParameters;
import hu.lae.usermanagement.UserInfo;

@Theme("lae")
public class LaeUI extends UI {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(LaeUI.class);
	
	private ApplicationService applicationService = ((LaeServlet)VaadinServlet.getCurrent()).applicationService;
	
	private UserInfo userInfo;

	@Override
	protected void init(VaadinRequest request) {

        logger.debug("ErpUI initialized");
        
        if(userInfo == null) {
            showLogin();
        }
	}
	
	private void showLogin() {
	    LoginWindow loginWindow = new LoginWindow(applicationService.authenticator, this::buildUI);
	    addWindow(loginWindow);
	}
	
	private void buildUI(UserInfo userInfo) {
	    RiskParametersPanel riskParametersPanel = new RiskParametersPanel(applicationService.riskParameterRepository);
	    
	    BalanceSheetPanel balanceSheetPanel = new BalanceSheetPanel(BalanceSheet.createEmpty());
	    IncomeStatementPanel incomeStatementPanel = new IncomeStatementPanel(IncomeStatement.createEmpty(LocalDate.now().getYear()-1));
	    HorizontalLayout mainLayout = new HorizontalLayout(riskParametersPanel, balanceSheetPanel, incomeStatementPanel);
	    mainLayout.setSpacing(true);
	    mainLayout.setMargin(true);
	    
	    Button calculateButton = new Button("Calculate");
	    calculateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	    calculateButton.addStyleName(ValoTheme.BUTTON_LARGE);
	    
	    calculateButton.addClickListener(click -> {
	        RiskParameters riskParameters = applicationService.riskParameterRepository.loadRiskParameters();
	        MaxLoanDistributor maxLoanDistributor = new LoanCalculator(riskParameters).createMaxLoanDistributor(balanceSheetPanel.getBalanceSheet(), incomeStatementPanel.getIncomeStatement());
	        UI.getCurrent().addWindow(new CalculatorWindow(maxLoanDistributor));
	    });
	    
	    VerticalLayout pageLayout = new VerticalLayout(new Header(userInfo), new Menu(), mainLayout, calculateButton);
	    pageLayout.setComponentAlignment(calculateButton, Alignment.MIDDLE_CENTER);
		setContent(pageLayout);
	}
	
	public static LaeUI getCurrent() {
		return (LaeUI)UI.getCurrent();
	}
	
}