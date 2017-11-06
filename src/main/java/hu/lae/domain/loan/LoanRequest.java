package hu.lae.domain.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LoanRequest {

    public final double shortTermLoan;
    
    public final double longTermLoan;
    
    public final LocalDate requestDate;
    
    public final LocalDate longTermLoanMaturityDate;
    
    public LoanRequest(double shortTermLoan, double longTermLoan, LocalDate requestDate, LocalDate longTermLoanMaturityDate) {
        this.shortTermLoan = shortTermLoan;
        this.longTermLoan = longTermLoan;
        this.requestDate = requestDate;
        this.longTermLoanMaturityDate = longTermLoanMaturityDate;
    }
    
    public double maturityYears() {
        long days = ChronoUnit.DAYS.between(requestDate, longTermLoanMaturityDate);
        return days / 365.0;
    }
    
    public double sum() {
        return shortTermLoan + longTermLoan;
    }
    
    public List<Loan> toLoans() {
        List<Loan> loans = new ArrayList<>();
        if(shortTermLoan > 0) {
            loans.add(new Loan(true, shortTermLoan, LoanType.ShortTerm, true, false));
        }
        if(longTermLoan > 0) {
            loans.add(new Loan(true, longTermLoan, LoanType.LongTerm, true, false));
        }
        return loans;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
