package hu.lae.loan;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.accounting.CashFlow;
import hu.lae.riskparameters.InterestRate;
import hu.lae.util.MathUtil;

public class MaxLoanDistributor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final long justifiableShortTermloan;
    
    public final long cashFlowElement;
    
    public final InterestRate shortTermInterestRate;
    
    public final InterestRate longTermInterestRate;
    
    public final long ebitda;
    
    private final BigDecimal dscrThreshold;
    
    public MaxLoanDistributor(InterestRate shortTermInterestRate, long justifiableShortTermloan, long cashFlowElement, InterestRate longTermInterestRate, long ebitda, BigDecimal dscrThreshold) {
        this.shortTermInterestRate = shortTermInterestRate;
        this.justifiableShortTermloan = justifiableShortTermloan;
        this.cashFlowElement = cashFlowElement;
        this.longTermInterestRate = longTermInterestRate;
        this.ebitda = ebitda;
        this.dscrThreshold = dscrThreshold;
    }
    
    public long calculateMaxShortTermLoan(int paybackYears) {
        long maxShortTermLoan = justifiableShortTermloan + calculateMaxLongTermLoan(paybackYears, justifiableShortTermloan); 
        logger.debug("Max short term loan: " + maxShortTermLoan + " = " + justifiableShortTermloan + " + maxLongTermLoan(" + paybackYears + ", " + justifiableShortTermloan + ")");
        return maxShortTermLoan;
    }
    
    public long calculateMaxLongTermLoan(int paybackYears, long shortTermLoanAmount) {
        if(shortTermLoanAmount <= justifiableShortTermloan) {
            logger.debug("Calculating long term debt capacity for " + shortTermLoanAmount + " short term loan and " + paybackYears + " years payback");
            BigDecimal interestForSTLoan = shortTermInterestRate.multiply(new BigDecimal(shortTermLoanAmount));
            logger.debug("Interest for short term loan " + shortTermLoanAmount + ": " + interestForSTLoan + " = " + shortTermInterestRate + " * " + shortTermLoanAmount);
            BigDecimal freeCashFlowElement = MathUtil.div(new BigDecimal(cashFlowElement).setScale(2), dscrThreshold).subtract(interestForSTLoan);
            logger.debug("Free cash flow: " + freeCashFlowElement + " = " + cashFlowElement + " / " + dscrThreshold + " - " + interestForSTLoan);
            CashFlow cashFlow = new CashFlow(paybackYears, freeCashFlowElement);
            BigDecimal ltDebtCapacity = cashFlow.presentValue(longTermInterestRate);
            logger.debug("Long term debt capacity: " + ltDebtCapacity + " = PV(" + freeCashFlowElement + ", " + paybackYears + ", " + longTermInterestRate + ")");
            
            return ltDebtCapacity.min(BigDecimal.valueOf(ebitda * 5)).setScale(0, RoundingMode.HALF_UP).longValue();
        } else {
            return calculateMaxLongTermLoan(paybackYears, justifiableShortTermloan) - (shortTermLoanAmount - justifiableShortTermloan);
        }
        
    }
}
