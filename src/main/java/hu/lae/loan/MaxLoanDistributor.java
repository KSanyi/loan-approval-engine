package hu.lae.loan;

import java.math.BigDecimal;

import hu.lae.accounting.CashFlow;
import hu.lae.riskparameters.InterestRate;

public class MaxLoanDistributor {

    public final long maxShortTermloan;
    
    public final long cashFlowElement;
    
    public final InterestRate shortTermInterestRate;
    
    public final InterestRate longTermInterestRate;
    
    public final long ebitda;
    
    public MaxLoanDistributor(InterestRate shortTermInterestRate, long maxShortTermloan, long cashFlowElement, InterestRate longTermInterestRate, long ebitda) {
        this.shortTermInterestRate = shortTermInterestRate;
        this.maxShortTermloan = maxShortTermloan;
        this.cashFlowElement = cashFlowElement;
        this.longTermInterestRate = longTermInterestRate;
        this.ebitda = ebitda;
    }
    
    public long maxLongTermLoan(int paybackYears, long shortTermLoanAmount) {
        long interestForSTLoan = shortTermInterestRate.multiply(new BigDecimal(shortTermLoanAmount)).longValue();
        CashFlow cashFlow = new CashFlow(paybackYears, cashFlowElement - interestForSTLoan);
        BigDecimal ltDebtCapacity = cashFlow.presentValue(longTermInterestRate);
        
        return ltDebtCapacity.min(BigDecimal.valueOf(ebitda * 5)).longValue();
    }
}
