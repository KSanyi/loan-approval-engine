package hu.lae.infrastructure.ui.loancalculation;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.NumberRenderer;

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
    
    private final RiskParameters riskParameters;
    
    private final Client client;
    
    private final LoanRequest loanRequest;
    
    DecisionWindow(RiskParameters riskParameters, Client client, LoanRequest loanRequest) {
        this.riskParameters= riskParameters;
        this.client = client;
        this.loanRequest = loanRequest;
        
        setModal(true);
        setCaption("Decision");
        setContent(createLayout());
        setSizeUndefined();
        
        addShortcutListener(VaadinUtil.createErrorSubmissionShortcutListener());
    }
    
    private Layout createLayout() {
        HorizontalLayout firstRow = new HorizontalLayout(createEbitdaTable(), createWarningsTable());
        VerticalLayout layout = new VerticalLayout(firstRow);
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
                new EbitdaTableRow("EBITDA",ebitdas, client.financialHistory.incomeStatementHistory().ebitdaGrowt()),
                new EbitdaTableRow("Sales", sales, client.financialHistory.incomeStatementHistory().salesGrowt()));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("EBITDA & Sales & Free CF");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(2);
        
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
        
        EquityRatioValidator equityRatioValidator = new EquityRatioValidator(riskParameters.thresholds.equityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> equityRatios = client.financialHistory.financialStatements.stream()
            .collect(Collectors.toMap(f -> f.year, f -> {
            double equityRatio = f.balanceSheet.liabilities.equityRatio();
            return new Pair<>(PF.format(equityRatio), equityRatioValidator.validate(f));   
        }));
        
        LiquidityValidator liquidityRatioValidator = new LiquidityValidator(client.existingLoans.shortTermLoans, riskParameters.thresholds.liquidityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double equityRatio = f.balanceSheet.liabilities.equityRatio();
                return new Pair<>(PF.format(equityRatio), liquidityRatioValidator.validate(f, loanRequest));   
            }));
        
        Map<Integer, Pair<String, ValidationResult>> supplierDaysValues = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(DF.format(f.supplierDays()), ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> buyersDays = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>("0.0", ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> stockDays = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>("0.0", ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> equityValues = client.financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>("0.0", ValidationResult.Ok());   
        }));
        
        List<WariningTableRow> items = Arrays.asList(
                new WariningTableRow("Equity value", equityValues),
                new WariningTableRow("Equity ratio", equityRatios),
                new WariningTableRow("Liquidity ratio", liquidityRatios),
                new WariningTableRow("Suppliers day", supplierDaysValues),
                new WariningTableRow("Buyers days", buyersDays),
                new WariningTableRow("Stock days", stockDays));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("Warnings & KOs");
        grid.setHeightMode(HeightMode.ROW);
        //grid.setWidth("250px");
        grid.setHeightByRows(6);
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
}
