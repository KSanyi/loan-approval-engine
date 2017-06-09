package hu.lae.domain.accounting;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IncomeStatementHistory {

    private final Map<Integer, IncomeStatement> incomeStatements;
    
    private final int lastYear;
    
    public IncomeStatementHistory(Map<Integer, IncomeStatement> incomeStatements) {
        
        this.incomeStatements = incomeStatements;
        
        lastYear = incomeStatements.keySet().stream().mapToInt(y -> y.intValue()).max().getAsInt();
    }

    public Stream<IncomeStatement> incomeStatements() {
        return incomeStatements.values().stream();
    }

    public IncomeStatement lastIncomeStatement() {
        return incomeStatements.get(lastYear);
    }
    
    public Map<Integer, Long> ebitdas() {
        return toMap(i -> i.ebitda);
    }
    
    public Map<Integer, Long> sales() {
        return toMap(i -> i.sales);
    }
    
    private Map<Integer, Long> toMap(Function<IncomeStatement, Long> function) {
        return incomeStatements.keySet().stream().collect(Collectors.toMap(year -> year, year -> function.apply(incomeStatements.get(year)))); 
    }
    
    public double ebitdaGrowth() {
        return (double)incomeStatements.get(lastYear).ebitda / incomeStatements.get(lastYear - 1).ebitda - 1; 
    }
    
    public double salesGrowth() {
        return (double)incomeStatements.get(lastYear).sales / incomeStatements.get(lastYear - 1).sales - 1; 
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
