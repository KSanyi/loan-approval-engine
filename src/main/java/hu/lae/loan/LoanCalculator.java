package hu.lae.loan;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.bankmonitor.mortgage.evaluation.calculation.ExcelFunctions;
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
        
        double justifiableShortTermLoan = balanceSheet.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        logger.debug("Justifiable short term loan: " + justifiableShortTermLoan);

        double freeCashFlow = incomeStatement.normalizedFreeCashFlow(riskParameters.amortizationRate);
        logger.debug("Normalized free cash flow: " + freeCashFlow);
        
        InterestRate shortTermInterestRate = riskParameters.shortTermInterestRate;
        
        double effectiveJustifiableSTLoan = Math.min(justifiableShortTermLoan, freeCashFlow / shortTermInterestRate.multiply(riskParameters.dscrThreshold));
        
        logger.debug("Justifiable short term loan: " + effectiveJustifiableSTLoan + " = Min(" + justifiableShortTermLoan + ", " + freeCashFlow + " / (" + riskParameters.dscrThreshold + " * " + shortTermInterestRate + "))");
        
        double maxShortTermLoan = effectiveJustifiableSTLoan + calculateMaxLongTermLoan(paybackYears, effectiveJustifiableSTLoan, effectiveJustifiableSTLoan, freeCashFlow, incomeStatement.ebitda); 
        logger.debug("Max short term loan: " + maxShortTermLoan + " = " + effectiveJustifiableSTLoan + " + maxLongTermLoan(" + paybackYears + ", " + effectiveJustifiableSTLoan + ")");
        
        double maxLongTermLoan = calculateMaxLongTermLoan(paybackYears, shortTermLoan, effectiveJustifiableSTLoan, freeCashFlow, incomeStatement.ebitda); 
        
        LoanApplicationResult result = new LoanApplicationResult(effectiveJustifiableSTLoan, maxShortTermLoan, maxLongTermLoan);
        
        logger.info("Calculation done: " + result);
        
        return result;
    }
    
    private double calculateMaxLongTermLoan(int paybackYears, double shortTermLoanAmount, double justifiableShortTermloan, double freeCashFlow, long ebitda) {
        
        double loanServiceCF = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, riskParameters.maxLoanDuration, shortTermLoanAmount - justifiableShortTermloan);
        logger.debug("Cash flow for loan service: " + loanServiceCF);
        double cashFlowForLongtermLoans = freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermloan) - 0 - loanServiceCF;
        logger.debug("Cash flow for remaining for long term loans: " + cashFlowForLongtermLoans);
        
        return new CashFlow(paybackYears, cashFlowForLongtermLoans).presentValue(riskParameters.longTermInterestRate);
    }
    
    
    @SuppressWarnings("unused")
    private double calculateMaxLongTermLoanOld(int paybackYears, double shortTermLoanAmount, double justifiableShortTermloan, double freeCashFlow, long ebitda) {
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
