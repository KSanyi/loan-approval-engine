package hu.lae.loan;

import java.math.BigDecimal;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.IncomeStatement;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;
import hu.lae.util.MathUtil;

public class LoanCalculator {

    private final RiskParameters riskParameters;

    public LoanCalculator(RiskParameters riskParameters) {
        this.riskParameters = riskParameters;
    }
    
    public MaxLoanDistributor createMaxLoanDistributor(BalanceSheet balanceSheet, IncomeStatement incomeStatement) {
        
        BigDecimal xxx = balanceSheet.calculateXXX(riskParameters.haircuts);

        BigDecimal freeCashFlow = incomeStatement.normalizedFreeCashFlow(riskParameters.amortizationRate);
        
        InterestRate shortTermInterestRate = riskParameters.shortTermInterestRate;
        
        long justifiableSTLoan = xxx.min(MathUtil.div(freeCashFlow, shortTermInterestRate.multiply(riskParameters.dscrThreshold))).longValue();
        
        return new MaxLoanDistributor(shortTermInterestRate, justifiableSTLoan, freeCashFlow.longValue(), riskParameters.longTermInterestRate, incomeStatement.ebitda);
    }
    
}
