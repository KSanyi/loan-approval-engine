package hu.lae.loan;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.Client;
import hu.lae.accounting.CashFlow;
import hu.lae.accounting.FreeCashFlowCalculator;
import hu.lae.riskparameters.Industry;
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
    
    public LoanApplicationResult calculate(Client client, int paybackYears, double shortTermLoan, FreeCashFlowCalculator freeCashFlowCalculator, boolean refinanceExistingLongTermLoans) {
        
        logger.debug("------------------------- Calculation starts -------------------------");
        logger.debug(client.toString());
        logger.debug("Payback years: " + paybackYears + ", short term loan: " + shortTermLoan);
        
        double justifiableShortTermLoan = client.balanceSheet.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        logger.debug("Justifiable short term loan: " + justifiableShortTermLoan);

        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementData, riskParameters.amortizationRate);
        logger.debug("Normalized free cash flow: " + freeCashFlow);
        
        InterestRate shortTermInterestRate = riskParameters.shortTermInterestRate;
        
        double effectiveJustifiableSTLoan = Math.min(justifiableShortTermLoan, freeCashFlow / shortTermInterestRate.multiply(riskParameters.dscrThreshold));
        
        logger.debug("Justifiable short term loan: " + effectiveJustifiableSTLoan + " = Min(" + justifiableShortTermLoan + ", " + freeCashFlow + " / (" + riskParameters.dscrThreshold + " * " + shortTermInterestRate + "))");
        
        double maxShortTermLoan = effectiveJustifiableSTLoan + calculateMaxLongTermLoan(paybackYears, effectiveJustifiableSTLoan, effectiveJustifiableSTLoan, freeCashFlow, client.existingLoans, client.industry, refinanceExistingLongTermLoans); 
        logger.debug("Max short term loan: " + maxShortTermLoan + " = " + effectiveJustifiableSTLoan + " + maxLongTermLoan(" + paybackYears + ", " + effectiveJustifiableSTLoan + ")");
        
        double maxLongTermLoan = calculateMaxLongTermLoan(paybackYears, shortTermLoan, effectiveJustifiableSTLoan, freeCashFlow, client.existingLoans, client.industry, refinanceExistingLongTermLoans); 
        
        LoanApplicationResult result = new LoanApplicationResult(effectiveJustifiableSTLoan, maxShortTermLoan, maxLongTermLoan);
        
        logger.info("Calculation done: " + result);
        
        return result;
    }
    
    private double calculateMaxLongTermLoan(int paybackYears, double shortTermLoanAmount, double justifiableShortTermloan, double freeCashFlow, ExistingLoans existingLoans, Industry industry, boolean refinanceExistingLongTermLoans) {
        
        double cashFlowForNewLongTermLoans;
        
        if(shortTermLoanAmount >= justifiableShortTermloan) {
            double amountAboveJustifiableSTLoan = shortTermLoanAmount - justifiableShortTermloan;
            logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(industry);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermloan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(shortTermLoanAmount));
        }
        
        logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        if(!refinanceExistingLongTermLoans) {
            double yealyDebtServiceForExistingLoans = existingLoans.yealyDebtService(riskParameters.longTermInterestRate, currentDate);
            cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yealyDebtServiceForExistingLoans);
        }
        
        return new CashFlow(paybackYears, cashFlowForNewLongTermLoans).presentValue(riskParameters.longTermInterestRate); 
    }
    
}
