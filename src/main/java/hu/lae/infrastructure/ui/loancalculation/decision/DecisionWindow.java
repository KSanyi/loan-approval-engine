package hu.lae.infrastructure.ui.loancalculation.decision;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.legal.LegalEvaluationResult;
import hu.lae.domain.legal.LegalParameters;
import hu.lae.domain.loan.CovenantCalculator;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.Loan;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.loancalculation.proposal.ProposalWindow;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
public class DecisionWindow extends Window {

    private final RiskParameters riskParameters;
    
    private final LegalParameters legalParameters;
    
    private final Client client;
    
    private final LoanRequest loanRequest;
    
    private final double maxDebtCapacity;
    
    private final ExistingLoansRefinancing existingLoansRefinancing;
    
    private final double dscr;
    
    private final ProposalWindow proposalWindow;
    
    private final DebtServiceChart debtServiceChart;
    
    private final Button backButton = new Button("Back", click -> back());
    
    public DecisionWindow(RiskParameters riskParameters, LegalParameters legalParameters, Client client, ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest, double dscr, double maxDebtCapacity, double freeCashFlow, ProposalWindow proposalWindow) {
        this.riskParameters= riskParameters;
        this.legalParameters = legalParameters;
        this.client = client;
        this.existingLoansRefinancing = existingLoansRefinancing;
        this.maxDebtCapacity = maxDebtCapacity;
        this.loanRequest = loanRequest;
        this.dscr = dscr;
        this.proposalWindow = proposalWindow;
        
        debtServiceChart = new DebtServiceChart(freeCashFlow, existingLoansRefinancing, loanRequest, riskParameters.interestRates);
        
        setModal(true);
        setCaption("Decision");
        setContent(createLayout());
        
        addShortcutListener(VaadinUtil.createErrorSubmissionShortcutListener());
    }
    
    private void back() {
        close();
        UI.getCurrent().addWindow(proposalWindow);
    }

    private Layout createLayout() {
        
        TextField dscrField = new TextField("DSCR", Formatters.formatPercent2(dscr/100));
        dscrField.setWidth("100px");
        dscrField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
        dscrField.setReadOnly(true);
        
        VerticalLayout column1 = new VerticalLayout(createEbitdaTable(), createFreeCFTable(), createEquityRatioTable(), dscrField, createAllLoansTable(), createLegalEvaluationResultPanel());
        column1.setMargin(false);
        
        VerticalLayout column2 = new VerticalLayout(createWarningsTable(), createCovenantTable());
        column2.setMargin(false);
        
        HorizontalLayout columnsLayout = new HorizontalLayout(column1, column2);
        
        backButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        VerticalLayout layout = new VerticalLayout(columnsLayout, debtServiceChart, backButton);
        layout.setComponentAlignment(debtServiceChart, Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(backButton, Alignment.BOTTOM_CENTER);
        
        return layout;
    }
    
    private Grid<EbitdaTableRow> createEbitdaTable() {
        
        return new EbitdaTable(client.financialHistory);
    }
    
    private LegalEvaluationResultPanel createLegalEvaluationResultPanel() {
    	
    	LegalEvaluationResult legalEvaluationResult = legalParameters.evaluate(client.legalData);
    	return new LegalEvaluationResultPanel(legalEvaluationResult);
    }
    
    private FreeCashFlowTable createFreeCFTable() {
        double lastYearBasedFCF = FreeCashFlowCalculator.lastYear.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        double averageBasedFCF = FreeCashFlowCalculator.average.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        return new FreeCashFlowTable(lastYearBasedFCF, averageBasedFCF);
    }
    
    private EquityRatioTable createEquityRatioTable() {
        double equityRatioBeforeLoan = client.financialHistory.lastFinancialStatementData().balanceSheet.liabilities.equityRatio();
        double loanIncrement = loanRequest.sum() - existingLoansRefinancing.sumOfRefinancableLoans();
        double equityRatioAfterLoan = client.financialHistory.lastFinancialStatementData().balanceSheet.liabilities.equityRatio(loanIncrement);
        return new EquityRatioTable(equityRatioBeforeLoan, equityRatioAfterLoan);
    }
    
    private CovenantTable createCovenantTable() {
        
        CovenantCalculator covenantCalculator = new CovenantCalculator(riskParameters.thresholds.turnoverRequirement);
        double turnoverReqValue = covenantCalculator.calculateRequiredTurnover(existingLoansRefinancing, loanRequest, client.financialStatementData().incomeStatement.sales);
        double debtCapacityUsage = CovenantCalculator.calculateDebtCapacityUsage(loanRequest, maxDebtCapacity, existingLoansRefinancing);
        double localLoansRatio = covenantCalculator.calculateLocalLoansRatio(existingLoansRefinancing, loanRequest);
        double allLocalLoans = covenantCalculator.allLocalLoans(existingLoansRefinancing, loanRequest);
        String collateralRequirement = riskParameters.collateralRequirement.evaluate(client.pd, (long)allLocalLoans, debtCapacityUsage);

        return new CovenantTable(turnoverReqValue, debtCapacityUsage, localLoansRatio, allLocalLoans, collateralRequirement, riskParameters.thresholds);
    }
    
    private Grid<Loan> createAllLoansTable() {
        Grid<Loan> grid = new Grid<>("Loans");
        grid.addColumn(l -> l.loanType.name() + (l.refinanced ? " - refinanced" : "")).setCaption("Type");
        grid.addColumn(l -> Formatters.formatAmount(l.amount)).setCaption("Amount").setWidth(90).setStyleGenerator(item -> "v-align-right");
        grid.addColumn(l -> l.isOwn ? VaadinIcons.HOME.getHtml() : "").setCaption("Own").setRenderer(new HtmlRenderer()).setWidth(80).setStyleGenerator(item -> "v-align-center");
        grid.addColumn(l -> l.isNew ? VaadinIcons.STAR.getHtml() : "").setCaption("New").setRenderer(new HtmlRenderer()).setWidth(80).setStyleGenerator(item -> "v-align-center");

        grid.setSelectionMode(SelectionMode.NONE);
        
        List<Loan> loans = new ArrayList<>();
        loans.addAll(loanRequest.toLoans());
        loans.addAll(existingLoansRefinancing.toLoans());
        
        grid.setItems(loans);
        
        grid.setHeightByRows(loans.size());
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        return grid;
    }
    
    private WarningTable createWarningsTable() {
        
        return new WarningTable(riskParameters.thresholds, client.financialHistory, loanRequest, existingLoansRefinancing);
    }
    
}
