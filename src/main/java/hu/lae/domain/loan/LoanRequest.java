package hu.lae.domain.loan;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LoanRequest {

    public final double shortTermLoan;
    
    public final double longTermLoan;

    public LoanRequest(double shortTermLoan, double longTermLoan) {
        this.shortTermLoan = shortTermLoan;
        this.longTermLoan = longTermLoan;
    }
    
    public double sum() {
        return shortTermLoan + longTermLoan;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
