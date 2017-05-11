package hu.lae.accounting;

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
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
