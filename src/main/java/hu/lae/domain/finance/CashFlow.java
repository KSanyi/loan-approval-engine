package hu.lae.domain.finance;

import java.util.Collections;
import java.util.List;

import hu.lae.domain.riskparameters.InterestRate;

public class CashFlow {

    private final List<Double> values;

    public CashFlow(List<Double> values) {
        this.values = Collections.unmodifiableList(values);
    }
    
    public CashFlow(int numberOfElements, Double value) {
        this.values = Collections.nCopies(numberOfElements, value);
    }
    
    public double presentValue(InterestRate rate) {
        double sum = 0;
        for(int i=0;i<values.size();i++) {
            sum += rate.discount(values.get(i), i+1);
        }
        return sum;
    }

}
