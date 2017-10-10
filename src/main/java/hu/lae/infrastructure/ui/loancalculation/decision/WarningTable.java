package hu.lae.infrastructure.ui.loancalculation.decision;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;

import hu.lae.domain.finance.FinancialHistory;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.domain.validation.EquityRatioValidator;
import hu.lae.domain.validation.LiquidityValidator;
import hu.lae.domain.validation.ValidationResult;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;
import hu.lae.util.Pair;

@SuppressWarnings("serial")
class WarningTable extends Grid<WarningTableRow> {

    public WarningTable(Thresholds thresholds, FinancialHistory financialHistory, LoanRequest loanRequest, ExistingLoansRefinancing existingLoansRefinancing) {
        List<Integer> years = financialHistory.years();
        
        addColumn(row -> row.caption).setCaption("");
        years.stream().forEach(year -> addColumn(row -> row.values.get(year).v1)
                .setCaption(year + "")
                .setStyleGenerator(row -> row.values.get(year).v2.type.name()));
        
        //grid.setDescriptionGenerator(g -> g.validationResult.toString());
        
        Map<Integer, Pair<String, ValidationResult>> ownEquityValues = financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double ownEquity = f.balanceSheet.liabilities.ownEquity;
                return new Pair<>(Formatters.formatAmount(ownEquity), ValidationResult.Ok());   
            }));
        
        EquityRatioValidator equityRatioValidator = new EquityRatioValidator(thresholds.equityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> equityRatios = financialHistory.financialStatements.stream()
            .collect(Collectors.toMap(f -> f.year, f -> {
            double equityRatio = f.balanceSheet.liabilities.equityRatio();
            return new Pair<>(Formatters.formatPercent(equityRatio), equityRatioValidator.validate(f));   
        }));
        
        LiquidityValidator liquidityRatioValidator = new LiquidityValidator(existingLoansRefinancing.nonRefinancableShortTermLoans(), thresholds.liquidityRatio);
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios1 = financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio1(existingLoansRefinancing.nonRefinancableShortTermLoans() + loanRequest.shortTermLoan);
                
                return financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(Formatters.formatDecimal(liquidityRatio), liquidityRatioValidator.validateRatio1(f, loanRequest)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios2 = financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio2();
                return financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(Formatters.formatDecimal(liquidityRatio), liquidityRatioValidator.validateRatio2(f)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> liquidityRatios3 = financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                double liquidityRatio = f.balanceSheet.liquidityRatio3();
                return financialHistory.lastFinancialStatementData().year == f.year ? 
                        new Pair<>(Formatters.formatDecimal(liquidityRatio), liquidityRatioValidator.validateRatio3(f)) : new Pair<>("NA", ValidationResult.Ok());
            }));
        
        Map<Integer, Pair<String, ValidationResult>> supplierDaysValues = financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(Formatters.formatDecimal(f.supplierDays()), ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> buyersDays = financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(Formatters.formatDecimal(f.buyersDays()), ValidationResult.Ok());   
        }));
        
        Map<Integer, Pair<String, ValidationResult>> stockDays = financialHistory.financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> {
                return new Pair<>(Formatters.formatDecimal(f.stockDays()), ValidationResult.Ok());   
        }));
        
        List<WarningTableRow> items = Arrays.asList(
                new WarningTableRow("Equity value", ownEquityValues),
                new WarningTableRow("Equity ratio", equityRatios),
                new WarningTableRow("Liquidity ratio 1", liquidityRatios1),
                new WarningTableRow("Liquid. ratio 2 wo credit", liquidityRatios2),
                new WarningTableRow("Liquid. ratio 3 wo cred&cash", liquidityRatios3),
                new WarningTableRow("Suppliers day", supplierDaysValues),
                new WarningTableRow("Buyers days", buyersDays),
                new WarningTableRow("Stock days", stockDays));
        setItems(items);
        
        addStyleName(VaadinUtil.GRID_SMALL);
        setCaption("Warnings & KOs");
        setHeightMode(HeightMode.ROW);
        setHeightByRows(8);
        setSelectionMode(SelectionMode.NONE);
    }
    
}

class WarningTableRow {
    
    final String caption;
    final Map<Integer, Pair<String, ValidationResult>> values;

    public WarningTableRow(String caption, Map<Integer, Pair<String, ValidationResult>> values) {
        this.caption = caption;
        this.values = values;
    }
}


