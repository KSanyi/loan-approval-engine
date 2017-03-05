package hu.lae.loan;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.IncomeStatement;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;
import hu.lae.util.MathUtil;

public class LoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final RiskParameters riskParameters;

    public LoanCalculator(RiskParameters riskParameters) {
        this.riskParameters = riskParameters;
    }
    
    public MaxLoanDistributor createMaxLoanDistributor(BalanceSheet balanceSheet, IncomeStatement incomeStatement) {
        
        BigDecimal xxx = balanceSheet.calculateXXX(riskParameters.haircuts);
        logger.debug("xxx: " + xxx);

        BigDecimal freeCashFlow = incomeStatement.normalizedFreeCashFlow(riskParameters.amortizationRate);
        logger.debug("Normalized free cash flow: " + freeCashFlow);
        
        InterestRate shortTermInterestRate = riskParameters.shortTermInterestRate;
        
        long justifiableSTLoan = xxx.min(MathUtil.div(freeCashFlow, shortTermInterestRate.multiply(riskParameters.dscrThreshold))).longValue();
        logger.debug("Justifiable short term loan: " + justifiableSTLoan + " = Min(" + xxx + ", " + freeCashFlow + " / (" + riskParameters.dscrThreshold + " * " + shortTermInterestRate + "))");
        
        return new MaxLoanDistributor(shortTermInterestRate, justifiableSTLoan, freeCashFlow.longValue(), riskParameters.longTermInterestRate, incomeStatement.ebitda, riskParameters.dscrThreshold);
    }
    
}
