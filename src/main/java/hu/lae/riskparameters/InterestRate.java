package hu.lae.riskparameters;

import java.math.BigDecimal;
import java.math.RoundingMode;

import hu.lae.util.MathUtil;

public class InterestRate {

    public final BigDecimal value;

    public InterestRate(BigDecimal value) {
        this.value = value;
    }
    
    public BigDecimal calculatePrincipal(BigDecimal interest) {
        return interest.divide(value, RoundingMode.HALF_UP);
    }
    
    public BigDecimal multiply(BigDecimal amount) {
        return value.multiply(amount);
    }
    
    public BigDecimal discount(BigDecimal amount, int years) {
        double discountRate = Math.pow(1+value.doubleValue(), years);
        return MathUtil.div(amount, BigDecimal.valueOf(discountRate));
    }
    
}
