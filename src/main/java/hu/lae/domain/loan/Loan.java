package hu.lae.domain.loan;

public class Loan {

    public final boolean isNew;
    
    public final double amount;
    
    public final LoanType loanType;
    
    public final boolean isOwn;
    
    public final boolean refinanced;

    public Loan(boolean isNew, double amount, LoanType loanType, boolean isOwn, boolean refinanced) {
        this.isNew = isNew;
        this.amount = amount;
        this.loanType = loanType;
        this.isOwn = isOwn;
        this.refinanced = refinanced;
    }
    
}
