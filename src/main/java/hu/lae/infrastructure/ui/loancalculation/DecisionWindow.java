package hu.lae.infrastructure.ui.loancalculation;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.NumberRenderer;

import hu.lae.domain.Client;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.infrastructure.ui.client.validation.LiquidityValidator;
import hu.lae.infrastructure.ui.client.validation.ValidationResult;

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
        
        List<Integer> years = client.incomeStatementData.incomeStatements().map(statement -> statement.year).collect(Collectors.toList());
        
        Grid<EbitdaTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("");
        grid.addColumn(row -> row.ebitdaT2).setCaption(years.get(0) + "");
        grid.addColumn(row -> row.ebitdaT1).setCaption(years.get(1) + "");
        grid.addColumn(row -> row.ebitdaT).setCaption(years.get(2) + "");
        grid.addColumn(row -> row.change, new NumberRenderer(new DecimalFormat("0.0%"))).setCaption("%");
        
        List<Long> ebitdas = client.incomeStatementData.ebitdas();
        List<Long> sales = client.incomeStatementData.sales();
        
        List<EbitdaTableRow> items = Arrays.asList(
                new EbitdaTableRow("EBITDA", ebitdas.get(0), ebitdas.get(1), ebitdas.get(2), client.incomeStatementData.ebitdaGrowt()),
                new EbitdaTableRow("Sales", sales.get(0), sales.get(1), sales.get(2), client.incomeStatementData.salesGrowt()));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("EBITDA & Sales & Free CF");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(2);
        
        return grid;
    }
    
    private Grid<WariningTableRow> createWarningsTable() {
        
        List<Integer> years = client.incomeStatementData.incomeStatements().map(statement -> statement.year).collect(Collectors.toList());
        
        Grid<WariningTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("");
        grid.addColumn(row -> row.value).setCaption(years.get(2) + "");
        grid.addColumn(row -> "").setCaption("").setStyleGenerator(row -> row.validationResult.type.name());
        
        grid.setDescriptionGenerator(g -> g.validationResult.toString());
        
        double equityRatio = client.balanceSheet.liabilities.equityRatio();
        String equityRatioString = PF.format(equityRatio);
        boolean equityRatioOk = equityRatio >= riskParameters.thresholds.equityRatio;
        ValidationResult equityValidationResult = equityRatioOk ? ValidationResult.Ok() : ValidationResult.Ko("");

        ValidationResult liquidityRatioValidation = new LiquidityValidator(riskParameters.thresholds.liquidityRatio).validate(client, loanRequest);
        
        double shortTermLoan = client.existingLoans.shortTermLoans + loanRequest.shortTermLoan;
        double liquidityRatio = client.balanceSheet.liquidityRatio1(shortTermLoan);
        String liquidityRatioString = DF.format(liquidityRatio);
        
        double supplierDays = client.supplierDays();
        String supplierDaysString = DF.format(supplierDays);
        
        List<WariningTableRow> items = Arrays.asList(
                new WariningTableRow("Equity value", "0.0", ValidationResult.Ok()),
                new WariningTableRow("Equity ratio", equityRatioString, equityValidationResult),
                new WariningTableRow("Liquidity ratio", liquidityRatioString, liquidityRatioValidation),
                new WariningTableRow("Suppliers day", supplierDaysString, ValidationResult.Ok()),
                new WariningTableRow("Buyers days", "0.0", ValidationResult.Ok()),
                new WariningTableRow("Stock days", "0.0", ValidationResult.Ok()));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("Warnings & KOs");
        grid.setHeightMode(HeightMode.ROW);
        grid.setWidth("250px");
        grid.setHeightByRows(6);
        
        return grid;
    }
    
    private static class EbitdaTableRow {
        
        final String caption;
        final long ebitdaT2;
        final long ebitdaT1;
        final long ebitdaT;
        final double change;

        public EbitdaTableRow(String caption, long ebitdaT2, long ebitdaT1, long ebitdaT, double change) {
            this.caption = caption;
            this.ebitdaT2 = ebitdaT2;
            this.ebitdaT1 = ebitdaT1;
            this.ebitdaT = ebitdaT;
            this.change = change;
        }
    }
    
    private static class WariningTableRow {
        
        final String caption;
        final String value;
        final ValidationResult validationResult;

        public WariningTableRow(String caption, String value, ValidationResult validationResult) {
            this.caption = caption;
            this.value = value;
            this.validationResult = validationResult;
        }
    }
    
}
