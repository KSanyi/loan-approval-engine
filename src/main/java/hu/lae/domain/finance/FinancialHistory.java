package hu.lae.domain.finance;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FinancialHistory {

    public final List<FinancialStatementData> financialStatements;

    public FinancialHistory(List<FinancialStatementData> financialStatements) {
        this.financialStatements = financialStatements;
    }
    
    public FinancialStatementData lastFinancialStatementData() {
        return financialStatements.stream()
                .sorted(Comparator.comparing(FinancialStatementData::year).reversed())
                .findFirst().get();
    }

    public IncomeStatementHistory incomeStatementHistory() {
        return new IncomeStatementHistory(financialStatements.stream()
                .collect(Collectors.toMap(f -> f.year, f -> f.incomeStatement)));
    }

    public List<Integer> years() {
        return financialStatements.stream().map(f -> f.year).sorted().collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "\n" + financialStatements.stream().map(FinancialStatementData::toString).collect(Collectors.joining("\n")) + "\n";
    }
    
}
