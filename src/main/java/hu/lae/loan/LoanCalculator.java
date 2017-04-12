package hu.lae.loan;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.CashFlow;
import hu.lae.accounting.IncomeStatement;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;
import hu.lae.util.ExcelFunctions;

public class LoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final RiskParameters riskParameters;
    
    private final LocalDate currentDate;

    public LoanCalculator(RiskParameters riskParameters, LocalDate currentDate) {
        this.riskParameters = riskParameters;
        this.currentDate = currentDate;
    }
    
    public LoanApplicationResult calculate(BalanceSheet balanceSheet, IncomeStatement incomeStatement, ExistingLoans existingLoans, int paybackYears, long shortTermLoan) {
        
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
        
        double maxShortTermLoan = effectiveJustifiableSTLoan + calculateMaxLongTermLoan(paybackYears, effectiveJustifiableSTLoan, effectiveJustifiableSTLoan, freeCashFlow, incomeStatement.ebitda, existingLoans); 
        logger.debug("Max short term loan: " + maxShortTermLoan + " = " + effectiveJustifiableSTLoan + " + maxLongTermLoan(" + paybackYears + ", " + effectiveJustifiableSTLoan + ")");
        
        double maxLongTermLoan = calculateMaxLongTermLoan(paybackYears, shortTermLoan, effectiveJustifiableSTLoan, freeCashFlow, incomeStatement.ebitda, existingLoans); 
        
        LoanApplicationResult result = new LoanApplicationResult(effectiveJustifiableSTLoan, maxShortTermLoan, maxLongTermLoan);
        
        logger.info("Calculation done: " + result);
        
        return result;
    }
    
    private double calculateMaxLongTermLoan(int paybackYears, double shortTermLoanAmount, double justifiableShortTermloan, double freeCashFlow, long ebitda, ExistingLoans existingLoans) {
        
        double cashFlowForNewLongTermLoans;
        
        if(shortTermLoanAmount >= justifiableShortTermloan) {
            double amountAboveJustifiableSTLoan = shortTermLoanAmount - justifiableShortTermloan;
            logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, riskParameters.maxLoanDuration, amountAboveJustifiableSTLoan);
            logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermloan) - cfNeededForStDebtService;
        } else {
            cashFlowForNewLongTermLoans = freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(shortTermLoanAmount);
        }
        
        logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        if(!existingLoans.isToBeRefinanced) {
            double yealyDebtServiceForExistingLoans = existingLoans.yealyDebtService(riskParameters.longTermInterestRate, currentDate);
            cashFlowForNewLongTermLoans -= yealyDebtServiceForExistingLoans;
        }
        
        return new CashFlow(paybackYears, cashFlowForNewLongTermLoans).presentValue(riskParameters.longTermInterestRate); 
    }
    
}
