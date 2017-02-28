package hu.lae.accounting;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import hu.lae.riskparameters.InterestRate;

public class CashFlow {

    private final List<Long> values;

    public CashFlow(List<Long> values) {
        this.values = Collections.unmodifiableList(values);
    }
    
    public CashFlow(int numberOfElements, long value) {
        this.values = Collections.nCopies(numberOfElements, value);
    }
    
    public BigDecimal presentValue(InterestRate rate) {
        BigDecimal sum = BigDecimal.ZERO;
        for(int i=0;i<values.size();i++) {
            sum = sum.add(rate.discount(BigDecimal.valueOf(values.get(i)), i+1));
        }
        return sum;
    }

}
