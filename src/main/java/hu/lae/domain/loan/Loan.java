package hu.lae.domain.loan;

public class Loan {

    public final boolean isNew;
    
    public final double amount;
    
    public final LoanType loanType;
    
    public final boolean isLocal;

    public Loan(boolean isNew, double amount, LoanType loanType, boolean isLocal) {
        this.isNew = isNew;
        this.amount = amount;
        this.loanType = loanType;
        this.isLocal = isLocal;
    }
    
}
