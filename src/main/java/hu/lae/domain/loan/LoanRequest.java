package hu.lae.domain.loan;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LoanRequest {

    public final double shortTermLoan;
    
    public final double longTermLoan;
    
    public final int longTermLoanDUration;

    public LoanRequest(double shortTermLoan, double longTermLoan, int longTermLoanDUration) {
        this.shortTermLoan = shortTermLoan;
        this.longTermLoan = longTermLoan;
        this.longTermLoanDUration = longTermLoanDUration;
    }
    
    public double sum() {
        return shortTermLoan + longTermLoan;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
