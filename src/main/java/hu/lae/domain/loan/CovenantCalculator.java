package hu.lae.domain.loan;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CovenantCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final double turnoverReqTolerance;
    
    public static double calculateDebtCapacityUsage(LoanRequest loanRequest, double maxDebtCapacity, ExistingLoansRefinancing existingLoansRefinancing) {
        
        double nonRefinancableExistingLoans = existingLoansRefinancing.nonRefinancableLoans();
        double debtCapacityUsage = (loanRequest.sum() + nonRefinancableExistingLoans) / maxDebtCapacity;

        logger.debug("Calculated Debt Capacity Usage: " + debtCapacityUsage);
        
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
        
        logger.debug("Calculated Required Turnover: " + requiredTurnover);
        
        return requiredTurnover;
    }
    
    public double calculateLocalLoansRatio(ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest) {
        
        double allLoans = existingLoansRefinancing.nonRefinancableLoans() + loanRequest.sum();
        
        double localLoans = allLocalLoans(existingLoansRefinancing, loanRequest);
        
        double localLoansRatio = localLoans / allLoans;
        
        logger.debug("Calculated local loans ratio: " + localLoansRatio);
        
        return localLoansRatio;
        
    }
    
    public double allLocalLoans(ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest) {
        
        return existingLoansRefinancing.localNonRefinancableLoans() +  + loanRequest.sum();
    }
    
}
