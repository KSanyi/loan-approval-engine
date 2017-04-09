package hu.lae.infrastructure.ui;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.IncomeStatement;
import hu.lae.infrastructure.server.ApplicationService;
import hu.lae.infrastructure.server.LaeServlet;
import hu.lae.infrastructure.ui.balancesheet.BalanceSheetPanel;
import hu.lae.infrastructure.ui.existingloans.ExistingLoansPanel;
import hu.lae.infrastructure.ui.incomestatement.IncomeStatementPanel;
import hu.lae.infrastructure.ui.loancalculation.CalculatorWindow;
import hu.lae.infrastructure.ui.riskparameters.RiskParametersPanel;
import hu.lae.loan.ExistingLoans;
import hu.lae.loan.LoanCalculator;
import hu.lae.riskparameters.RiskParameters;
import hu.lae.usermanagement.UserInfo;
import hu.lae.util.DateUtil;

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
	}
	
	private void showLogin() {
	    LoginWindow loginWindow = new LoginWindow(applicationService.authenticator, this::buildUI);
	    addWindow(loginWindow);
	}
	
	private void buildUI(UserInfo userInfo) {
	    logger.info(userInfo.loginName + " logged in");
	    
	    DateField currentDateField = new DateField("Current date");
	    currentDateField.setValue(DateUtil.convertToDate(LocalDate.of(2017, 4, 1)));
	    currentDateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
	    currentDateField.setWidth("120px");
	    
	    RiskParametersPanel riskParametersPanel = new RiskParametersPanel(applicationService.riskParameterRepository);
	    
	    BalanceSheetPanel balanceSheetPanel = new BalanceSheetPanel(BalanceSheet.createDefault());
	    IncomeStatementPanel incomeStatementPanel = new IncomeStatementPanel(IncomeStatement.createDefault(LocalDate.now().getYear()-1));
	    ExistingLoansPanel existingLoansPanel = new ExistingLoansPanel(ExistingLoans.createEmpty());
	    HorizontalLayout mainLayout = new HorizontalLayout(riskParametersPanel, balanceSheetPanel, incomeStatementPanel, existingLoansPanel);
	    mainLayout.setSpacing(true);
	    mainLayout.setMargin(true);
	    
	    Button calculateButton = new Button("Calculate");
	    calculateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	    calculateButton.addStyleName(ValoTheme.BUTTON_LARGE);
	    
	    calculateButton.addClickListener(click -> {
	        RiskParameters riskParameters = riskParametersPanel.getRiskParameters();
	        LocalDate currentDate = DateUtil.convertToLocalDate(currentDateField.getValue());
	        
	        CalculatorWindow calculatorWindow = new CalculatorWindow(new LoanCalculator(riskParameters, currentDate), balanceSheetPanel.getBalanceSheet(),
	                incomeStatementPanel.getIncomeStatement(), existingLoansPanel.getExistingLoans(), currentDate);
	        UI.getCurrent().addWindow(calculatorWindow);
	    });
	    
	    VerticalLayout pageLayout = new VerticalLayout(new Header(userInfo), new Menu(), currentDateField, mainLayout, calculateButton);
	    pageLayout.setComponentAlignment(currentDateField, Alignment.MIDDLE_CENTER);
	    pageLayout.setComponentAlignment(calculateButton, Alignment.MIDDLE_CENTER);
		setContent(pageLayout);
	}
	
	public static LaeUI getCurrent() {
		return (LaeUI)UI.getCurrent();
	}
	
}