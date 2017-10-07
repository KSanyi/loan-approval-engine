package hu.lae.infrastructure.ui.loancalculation.decision;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.grid.HeightMode;
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
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.loan.CovenantCalculator;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.Loan;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.validation.EquityRatioValidator;
import hu.lae.domain.validation.LiquidityValidator;
import hu.lae.domain.validation.ValidationResult;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.infrastructure.ui.component.Button;
import hu.lae.infrastructure.ui.loancalculation.proposal.ProposalWindow;
import hu.lae.util.Formatters;
import hu.lae.util.Pair;

@SuppressWarnings("serial")
public class DecisionWindow extends Window {

    private final RiskParameters riskParameters;
    
    private final Client client;
    
    private final LoanRequest loanRequest;
    
    private final double maxDebtCapacity;
    
    private final ExistingLoansRefinancing existingLoansRefinancing;
    
    private final double dscr;
    
    private final ProposalWindow proposalWindow;
    
    private final DebtServiceChart debtServiceChart;
    
    private final Button backButton = new Button("Back", click -> back());
    
    public DecisionWindow(RiskParameters riskParameters, Client client, ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest, double dscr, double maxDebtCapacity, double freeCashFlow, ProposalWindow proposalWindow) {
        this.riskParameters= riskParameters;
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
        
        VerticalLayout column1 = new VerticalLayout(createEbitdaTable(), createFreeCFTable(), dscrField, createAllLoansTable());
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
        
        List<Integer> years = client.financialHistory.years();
        
        Grid<EbitdaTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("");
        years.stream().forEach(year -> grid.addColumn(row -> row.ebitdas.get(year)).setCaption(year + ""));
        grid.addColumn(row -> row.lastChange, new NumberRenderer(new DecimalFormat("0.0%"))).setCaption("Last growth");
        
        Map<Integer, Long> ebitdas = client.financialHistory.incomeStatementHistory().ebitdas();
        Map<Integer, Long> sales = client.financialHistory.incomeStatementHistory().sales();
        
        List<EbitdaTableRow> items = Arrays.asList(
                new EbitdaTableRow("EBITDA",ebitdas, client.financialHistory.incomeStatementHistory().ebitdaGrowth()),
                new EbitdaTableRow("Sales", sales, client.financialHistory.incomeStatementHistory().salesGrowth()));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("EBITDA & Sales & Free CF");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(2);
        
        return grid;
    }
    
    private Grid<FreeCFTableRow> createFreeCFTable() {
        Grid<FreeCFTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("");
        grid.addColumn(row -> row.value).setCaption("Free CF");
        
        List<FreeCFTableRow> items = Arrays.asList(
                new FreeCFTableRow("W Last year", 0),
                new FreeCFTableRow("W Corrected", 0));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        //grid.setCaption("EBITDA & Sales & Free CF");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(2);
        grid.setWidth("250px");
        return grid;
    }
    
    private Grid<CovenantTableRow> createCovenantTable() {
        Grid<CovenantTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("Covenant");
        grid.addColumn(row -> row.value).setCaption("Value").setStyleGenerator(item -> "v-align-right");
        grid.addColumn(row -> row.message).setCaption("Contractual covenant");
        
        CovenantCalculator covenantCalculator = new CovenantCalculator(riskParameters.thresholds.turnoverRequirement);
        double turnoverReqValue = covenantCalculator.calculateRequiredTurnover(existingLoansRefinancing, loanRequest, client.financialStatementData().incomeStatement.sales);
        double debtCapacityUsage = CovenantCalculator.calculateDebtCapacityUsage(loanRequest, maxDebtCapacity, existingLoansRefinancing);
        double localLoansRatio = covenantCalculator.calculateLocalLoansRatio(existingLoansRefinancing, loanRequest);
        double allLocalLoans = covenantCalculator.allLocalLoans(existingLoansRefinancing, loanRequest);
        
        boolean furtherIndebtnessOk = debtCapacityUsage <= riskParameters.thresholds.debtCapacity;
        boolean localLoansRatioOk = localLoansRatio <= riskParameters.thresholds.localLoanRatio;
        
        List<CovenantTableRow> items = Arrays.asList(
                new CovenantTableRow("Turnover requirement", "", "Min HUF " + Formatters.formatAmount(turnoverReqValue) + " mln turnover"),
                new CovenantTableRow("Further indebtedness", Formatters.formatPercent(debtCapacityUsage), furtherIndebtnessOk ? "-" : "No further indebtedness"),
                new CovenantTableRow("Account opening clause", Formatters.formatDecimal(localLoansRatio), localLoansRatioOk ? "No restriction" : "Further accounts w bank consent"),
                new CovenantTableRow("Collateral requirement", "", riskParameters.collateralRequirement.evaluate(client.pd, (long)allLocalLoans, debtCapacityUsage)));

        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("Covenants");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(items.size());
        grid.setWidth("600px");
        return grid;
    }
    
    private Grid<Loan> createAllLoansTable() {
        Grid<Loan> grid = new Grid<>("Loans");
        grid.addColumn(l -> l.loanType.name() + (l.refinanced ? " - refinanced" : "")).setCaption("Type");
        grid.addColumn(l -> Formatters.formatAmount(l.amount)).setCaption("Amount").setWidth(90).setStyleGenerator(item -> "v-align-right");
        grid.addColumn(l -> l.isLocal ? VaadinIcons.HOME.getHtml() : "").setCaption("Own").setRenderer(new HtmlRenderer()).setWidth(80).setStyleGenerator(item -> "v-align-center");
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
    
    private Grid<WariningTableRow> createWarningsTable() {
        
        List<Integer> years = client.financialHistory.years();
        
        Grid<WariningTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("");
        years.stream().forEach(year -> grid.addColumn(row -> row.values.get(year).v1)
                .setCaption(year + "")
                .setStyleGenerator(row -> row.values.get(year).v2.type.name()));
        
        //grid.setDescriptionGenerator(g -> g.validationResult.toString());
        
        Map<Integer, Pair<String, ValidationResult>> ownEquityValues = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double ownEquity = f.balanceSheet.liabilities.ownEquity;
                return new Pair<>(Formatters.formatAmount(ownEquity), ValidationResult.Ok());   
            }));
        
        EquityRatioValidator equityRatioValidator = new EquityRatioValidator(riskParameters.thresholds.equityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> equityRatios = client.financialHistory.financialStatements.stream()
            .collect(Collectors.toMap(f -> f.year, f -> {
            double equityRatio = f.balanceSheet.liabilities.equityRatio();
            return new Pair<>(Formatters.formatAmount(equityRatio), equityRatioValidator.validate(f));   
        }));
        
        LiquidityValidator liquidityRatioValidator = new LiquidityValidator(existingLoansRefinancing.nonRefinancableShortTermLoans(), riskParameters.thresholds.liquidityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios1 = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio1(existingLoansRefinancing.nonRefinancableShortTermLoans() + loanRequest.shortTermLoan);
                
                return client.financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(Formatters.formatDecimal(liquidityRatio), liquidityRatioValidator.validateRatio1(f, loanRequest)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios2 = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio2();
                return client.financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(Formatters.formatDecimal(liquidityRatio), liquidityRatioValidator.validateRatio2(f)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios3 = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio3();
                return client.financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(Formatters.formatDecimal(liquidityRatio), liquidityRatioValidator.validateRatio3(f)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> supplierDaysValues = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(Formatters.formatDecimal(f.supplierDays()), ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> buyersDays = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(Formatters.formatDecimal(f.buyersDays()), ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> stockDays = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(Formatters.formatDecimal(f.stockDays()), ValidationResult.Ok());   
        }));
        
        List<WariningTableRow> items = Arrays.asList(
                new WariningTableRow("Equity value", ownEquityValues),
                new WariningTableRow("Equity ratio", equityRatios),
                new WariningTableRow("Liquidity ratio 1", liquidityRatios1),
                new WariningTableRow("Liquid. ratio 2 wo credit", liquidityRatios2),
                new WariningTableRow("Liquid. ratio 3 wo cred&cash", liquidityRatios3),
                new WariningTableRow("Suppliers day", supplierDaysValues),
                new WariningTableRow("Buyers days", buyersDays),
                new WariningTableRow("Stock days", stockDays));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("Warnings & KOs");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(8);
        grid.setSelectionMode(SelectionMode.NONE);
        
        return grid;
    }
    
    private static class EbitdaTableRow {
        
        final String caption;
        final Map<Integer, Long> ebitdas;
        final double lastChange;

        public EbitdaTableRow(String caption, Map<Integer, Long> ebitdas, double lastChange) {
            this.caption = caption;
            this.ebitdas = ebitdas;
            this.lastChange = lastChange;
        }
    }
    
    private static class WariningTableRow {
        
        final String caption;
        final Map<Integer, Pair<String, ValidationResult>> values;

        public WariningTableRow(String caption, Map<Integer, Pair<String, ValidationResult>> values) {
            this.caption = caption;
            this.values = values;
        }
    }
    
    private static class FreeCFTableRow {
        
        final String caption;
        final Integer value;

        public FreeCFTableRow(String caption, Integer value) {
            this.caption = caption;
            this.value = value;
        }
    }
    
    private static class CovenantTableRow {
        
        final String caption;
        final String value;
        final String message;

        public CovenantTableRow(String caption, String value, String message) {
            this.caption = caption;
            this.value = value;
            this.message = message;
        }
    }
}
