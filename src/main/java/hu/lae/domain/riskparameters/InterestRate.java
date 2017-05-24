package hu.lae.domain.riskparameters;

import com.ibm.icu.text.NumberFormat;

public class InterestRate {

    public final double value;

    public InterestRate(double value) {
        this.value = value;
    }
    
    public double calculatePrincipal(double interest) {
        return interest / value;
    }
    
    public double multiply(double amount) {
        return value * amount;
    }
    
    public double discount(double amount, int years) {
        double discountRate = Math.pow(1+value, years);
        return amount / discountRate;
    }
    
    @Override
    public String toString() {
        return NumberFormat.getPercentInstance().format(value);
    }
    
}
