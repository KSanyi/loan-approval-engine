package hu.lae.infrastructure.ui.loancalculation;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.validation.EquityRatioValidator;
import hu.lae.domain.validation.LiquidityValidator;
import hu.lae.domain.validation.ValidationResult;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Pair;

@SuppressWarnings("serial")
class DecisionWindow extends Window {

    private final static DecimalFormat DF = new DecimalFormat("0.00");
    private final static DecimalFormat PF = new DecimalFormat("0.0%");
    private final static DecimalFormat AF;
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator((char) 160);
        AF = new DecimalFormat("###,###", decimalFormatSymbols);
    }
    
    private final RiskParameters riskParameters;
    
    private final Client client;
    
    private final LoanRequest loanRequest;
    
    private final double dscr;
    
    DecisionWindow(RiskParameters riskParameters, Client client, LoanRequest loanRequest, double dscr) {
        this.riskParameters= riskParameters;
        this.client = client;
        this.loanRequest = loanRequest;
        this.dscr = dscr;
        
        setModal(true);
        setCaption("Decision");
        setContent(createLayout());
        setSizeUndefined();
        
        addShortcutListener(VaadinUtil.createErrorSubmissionShortcutListener());
    }
    
    private Layout createLayout() {
        
        TextField dscrField = new TextField("DSCR", PF.format(dscr/100));
        dscrField.setWidth("100px");
        dscrField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
        dscrField.setReadOnly(true);
        
        VerticalLayout column1 = new VerticalLayout(createEbitdaTable(), createFreeCFTable(), dscrField);
        column1.setMargin(false);
        
        VerticalLayout column2 = new VerticalLayout(createWarningsTable());
        column2.setMargin(false);
        
        HorizontalLayout layout = new HorizontalLayout(column1, column2);
        layout.setMargin(true);
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
                return new Pair<>(AF.format(ownEquity), ValidationResult.Ok());   
            }));
        
        EquityRatioValidator equityRatioValidator = new EquityRatioValidator(riskParameters.thresholds.equityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> equityRatios = client.financialHistory.financialStatements.stream()
            .collect(Collectors.toMap(f -> f.year, f -> {
            double equityRatio = f.balanceSheet.liabilities.equityRatio();
            return new Pair<>(PF.format(equityRatio), equityRatioValidator.validate(f));   
        }));
        
        LiquidityValidator liquidityRatioValidator = new LiquidityValidator(client.existingLoans.shortTermLoans, riskParameters.thresholds.liquidityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios1 = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio1(client.existingLoans.shortTermLoans + loanRequest.shortTermLoan);
                
                return client.financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(DF.format(liquidityRatio), liquidityRatioValidator.validateRatio1(f, loanRequest)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios2 = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio2();
                return client.financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(DF.format(liquidityRatio), liquidityRatioValidator.validateRatio2(f)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios3 = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio3();
                return client.financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(DF.format(liquidityRatio), liquidityRatioValidator.validateRatio3(f)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> supplierDaysValues = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(DF.format(f.supplierDays()), ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> buyersDays = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(DF.format(f.buyersDays()), ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> stockDays = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(DF.format(f.stockDays()), ValidationResult.Ok());   
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
}
