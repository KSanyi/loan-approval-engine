package hu.lae.domain.loan;

public class CovenantCalculator {

    private final double turnoverReqTolerance;
    
    public static double calculateDebtgCapacityUsage(LoanRequest loanRequest, double maxDebtCapacity, ExistingLoansRefinancing existingLoansRefinancing) {
        
        double nonRefinancableExistingLoans = existingLoansRefinancing.nonRefinancableLoans();
        double debtCapacityUsage = (loanRequest.sum() + nonRefinancableExistingLoans) / maxDebtCapacity;
        
        return debtCapacityUsage;
    }
    
    public CovenantCalculator(double turnoverReqTolerance) {
        this.turnoverReqTolerance = turnoverReqTolerance;
    }

    public double calculateRequiredTurnover(ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest, long turnover) {
        
        double allLoans = existingLoansRefinancing.nonRefinancableLoans() + loanRequest.sum();
        
        double localLoans = existingLoansRefinancing.localNonRefinancableLoans() +  + loanRequest.sum();
        
        double ratio = localLoans / allLoans;
        
        double requiredTurnover = ratio * turnover * (1 - turnoverReqTolerance);
        
        return requiredTurnover;
    }
    
    public double calculateLocalLoansRatio(ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest) {
        
        double allLoans = existingLoansRefinancing.nonRefinancableLoans() + loanRequest.sum();
        
        double localLoans = existingLoansRefinancing.localNonRefinancableLoans() +  + loanRequest.sum();
        
        return localLoans / allLoans;
        
    }
    
}
