package hu.lae.accounting;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import hu.lae.riskparameters.InterestRate;

public class CashFlow {

    private final List<BigDecimal> values;

    public CashFlow(List<BigDecimal> values) {
        this.values = Collections.unmodifiableList(values);
    }
    
    public CashFlow(int numberOfElements, BigDecimal value) {
        this.values = Collections.nCopies(numberOfElements, value);
    }
    
    public CashFlow(int numberOfElements, long value) {
        this.values = Collections.nCopies(numberOfElements, new BigDecimal(value));
    }
    
    public BigDecimal presentValue(InterestRate rate) {
        BigDecimal sum = BigDecimal.ZERO;
        for(int i=0;i<values.size();i++) {
            sum = sum.add(rate.discount(values.get(i), i+1));
        }
        return sum;
    }

}
