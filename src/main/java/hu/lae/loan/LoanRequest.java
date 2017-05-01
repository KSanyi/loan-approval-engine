package hu.lae.loan;

public class LoanRequest {

    public final double shortTermLoan;
    
    public final double longTermLoan;

    public LoanRequest(double shortTermLoan, double longTermLoan) {
        this.shortTermLoan = shortTermLoan;
        this.longTermLoan = longTermLoan;
    }
    
}
