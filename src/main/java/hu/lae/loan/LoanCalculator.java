package hu.lae.loan;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.CashFlow;
import hu.lae.accounting.IncomeStatement;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;

public class LoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final RiskParameters riskParameters;

    public LoanCalculator(RiskParameters riskParameters) {
        this.riskParameters = riskParameters;
    }
    
    public LoanApplicationResult calculate(BalanceSheet balanceSheet, IncomeStatement incomeStatement, int paybackYears, long shortTermLoan) {
        
        logger.debug("------------------------- Calculation starts -------------------------");
        logger.debug(balanceSheet + ", " + incomeStatement);
        logger.debug("Payback years: " + paybackYears + ", short term loan: " + shortTermLoan);
        
        double xxx = balanceSheet.calculateXXX(riskParameters.haircuts);
        logger.debug("xxx: " + xxx);

        double freeCashFlow = incomeStatement.normalizedFreeCashFlow(riskParameters.amortizationRate);
        logger.debug("Normalized free cash flow: " + freeCashFlow);
        
        InterestRate shortTermInterestRate = riskParameters.shortTermInterestRate;
        
        double justifiableSTLoan = Math.min(xxx, freeCashFlow / shortTermInterestRate.multiply(riskParameters.dscrThreshold));
        
        logger.debug("Justifiable short term loan: " + justifiableSTLoan + " = Min(" + xxx + ", " + freeCashFlow + " / (" + riskParameters.dscrThreshold + " * " + shortTermInterestRate + "))");
        
        double maxShortTermLoan = justifiableSTLoan + calculateMaxLongTermLoan(paybackYears, justifiableSTLoan, justifiableSTLoan, freeCashFlow, incomeStatement.ebitda); 
        logger.debug("Max short term loan: " + maxShortTermLoan + " = " + justifiableSTLoan + " + maxLongTermLoan(" + paybackYears + ", " + justifiableSTLoan + ")");
        
        double maxLongTermLoan = calculateMaxLongTermLoan(paybackYears, shortTermLoan, justifiableSTLoan, freeCashFlow, incomeStatement.ebitda); 
        
        LoanApplicationResult result = new LoanApplicationResult(justifiableSTLoan, maxShortTermLoan, maxLongTermLoan);
        
        logger.info("Calculation done: " + result);
        
        return result;
    }
    
    private double calculateMaxLongTermLoan(int paybackYears, double shortTermLoanAmount, double justifiableShortTermloan, double freeCashFlow, long ebitda) {
        if(shortTermLoanAmount <= justifiableShortTermloan) {
            logger.debug("Calculating long term debt capacity for " + shortTermLoanAmount + " short term loan and " + paybackYears + " years payback");
            double interestForSTLoan = riskParameters.shortTermInterestRate.multiply(shortTermLoanAmount);
            logger.debug("Interest for short term loan " + shortTermLoanAmount + ": " + interestForSTLoan + " = " + riskParameters.shortTermInterestRate + " * " + shortTermLoanAmount);
            double freeCashFlowElement = freeCashFlow / riskParameters.dscrThreshold - interestForSTLoan;
            logger.debug("Free cash flow: " + freeCashFlowElement + " = " + freeCashFlow + " / " + riskParameters.dscrThreshold + " - " + interestForSTLoan);
            CashFlow cashFlow = new CashFlow(paybackYears, freeCashFlowElement);
            double ltDebtCapacity = cashFlow.presentValue(riskParameters.longTermInterestRate);
            logger.debug("Long term debt capacity: " + ltDebtCapacity + " = PV(" + freeCashFlowElement + ", " + paybackYears + ", " + riskParameters.longTermInterestRate + ")");
            
            return Math.min(ltDebtCapacity, ebitda * 5);
        } else {
            return calculateMaxLongTermLoan(paybackYears, justifiableShortTermloan, justifiableShortTermloan, freeCashFlow, ebitda) - (shortTermLoanAmount - justifiableShortTermloan);
        }
        
    }
    
}
