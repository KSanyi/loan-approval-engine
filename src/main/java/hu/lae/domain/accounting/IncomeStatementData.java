package hu.lae.domain.accounting;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IncomeStatementData {

    private final List<IncomeStatement> incomeStatements;
    
    public IncomeStatementData(List<IncomeStatement> incomeStatements) {
        
        this.incomeStatements = Collections.unmodifiableList(incomeStatements);
    }

    public static IncomeStatementData createDefault(int i) {
        return new IncomeStatementData(Arrays.asList(
                IncomeStatement.createDefault(2014),
                IncomeStatement.createDefault(2015),
                IncomeStatement.createDefault(2016)));
    }
    
    public Stream<IncomeStatement> incomeStatements() {
        return incomeStatements.stream();
    }

    public IncomeStatement lastIncomeStatement() {
        return incomeStatements().sorted(Comparator.comparing(IncomeStatement::year).reversed()).findFirst().get();
    }
    
    public List<Long> ebitdas() {
        return incomeStatements().map(statement -> statement.ebitda).collect(Collectors.toList());
    }
    
    public List<Long> sales() {
        return incomeStatements().map(statement -> statement.sales).collect(Collectors.toList());
    }
    
    public double ebitdaGrowt() {
        int n = incomeStatements.size();
        return (double)incomeStatements.get(n-1).ebitda / incomeStatements.get(n-2).ebitda - 1; 
    }
    
    public double salesGrowt() {
        int n = incomeStatements.size();
        return (double)incomeStatements.get(n-1).sales / incomeStatements.get(n-2).sales - 1; 
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
