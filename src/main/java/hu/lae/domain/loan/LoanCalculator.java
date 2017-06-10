package hu.lae.domain.loan;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.Client;
import hu.lae.domain.accounting.CashFlow;
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.riskparameters.Industry;
import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.util.ExcelFunctions;

public class LoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final RiskParameters riskParameters;
    
    private final LocalDate currentDate;

    public LoanCalculator(RiskParameters riskParameters, LocalDate currentDate) {
        this.riskParameters = riskParameters;
        this.currentDate = currentDate;
    }
    
    public LoanRequest calculateIdealLoanRequest(Client client, FreeCashFlowCalculator freeCashFlowCalculator) {
        int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(client.industry);
        LoanApplicationResult result = calculate(client, maxLoanDuration, 0, freeCashFlowCalculator, true);
        
        double idealShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        result = calculate(client, maxLoanDuration, idealShortTermLoan, freeCashFlowCalculator, true);
        double idealLongTermLoan = result.maxLongTermLoan;
        
        LoanRequest idealLoanRequest = new LoanRequest(idealShortTermLoan, idealLongTermLoan);
        
        logger.info("Ideal loan request: " + idealLoanRequest);
        
        return idealLoanRequest;
    }
    
    public LoanApplicationResult calculate(Client client, int paybackYears, double shortTermLoan, FreeCashFlowCalculator freeCashFlowCalculator, boolean refinanceExistingLongTermLoans) {
        
        logger.debug("------------------------- Loan calculation starts -------------------------");
        // logger.debug(client.toString());
        // logger.debug("Payback years: " + paybackYears + ", short term loan: " + shortTermLoan + ", FreeCashFlowCalculator: " + freeCashFlowCalculator + ", refinanceExistingLongTermLoans: " + refinanceExistingLongTermLoans);
        
        double justifiableShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        // logger.debug("Justifiable short term loan: " + justifiableShortTermLoan);

        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        // logger.debug("Normalized free cash flow: " + freeCashFlow);
        
        InterestRate shortTermInterestRate = riskParameters.shortTermInterestRate;
        
        double effectiveJustifiableSTLoan = Math.min(justifiableShortTermLoan, freeCashFlow / shortTermInterestRate.multiply(riskParameters.dscrThreshold));
        // logger.debug("Justifiable short term loan: " + effectiveJustifiableSTLoan + " = Min(" + justifiableShortTermLoan + ", " + freeCashFlow + " / (" + riskParameters.dscrThreshold + " * " + shortTermInterestRate + "))");

        double maxShortTermLoan = effectiveJustifiableSTLoan + calculateMaxShortTermLoan(effectiveJustifiableSTLoan, freeCashFlow, client.existingLoans, client.industry, refinanceExistingLongTermLoans); 
        // logger.debug("Max short term loan: " + maxShortTermLoan + " = " + effectiveJustifiableSTLoan + " + maxLongTermLoan(" + paybackYears + ", " + effectiveJustifiableSTLoan + ")");
        
        double maxLongTermLoan = calculateMaxLongTermLoan(paybackYears, shortTermLoan, effectiveJustifiableSTLoan, freeCashFlow, client.existingLoans, client.industry, refinanceExistingLongTermLoans); 
        
        LoanApplicationResult result = new LoanApplicationResult(effectiveJustifiableSTLoan, maxShortTermLoan, maxLongTermLoan);
        
        logger.info("Calculation done: " + result);
        
        return result;
    }
    
    private double calculateMaxLongTermLoan(int paybackYears, double shortTermLoanAmount, double justifiableShortTermloan, double freeCashFlow, ExistingLoans existingLoans, Industry industry, boolean refinanceExistingLongTermLoans) {
        
        double cashFlowForNewLongTermLoans;
        
        if(shortTermLoanAmount >= justifiableShortTermloan) {
            double amountAboveJustifiableSTLoan = shortTermLoanAmount - justifiableShortTermloan;
            // logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(industry);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            // logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermloan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(shortTermLoanAmount));
        }
        
        // logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        if(!refinanceExistingLongTermLoans) {
            double yealyDebtServiceForExistingLoans = existingLoans.yealyDebtService(riskParameters.longTermInterestRate, currentDate);
            cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yealyDebtServiceForExistingLoans);
        }
        
        return new CashFlow(paybackYears, cashFlowForNewLongTermLoans).presentValue(riskParameters.longTermInterestRate); 
    }
    
    private double calculateMaxShortTermLoan(double justifiableShortTermloan, double freeCashFlow, ExistingLoans existingLoans, Industry industry, boolean refinanceExistingLongTermLoans) {
        
        double cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermloan));
        
        // logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        if(!refinanceExistingLongTermLoans) {
            double yealyDebtServiceForExistingLoans = existingLoans.yealyDebtService(riskParameters.longTermInterestRate, currentDate);
            cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yealyDebtServiceForExistingLoans);
        }
        
        int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(industry);
        return new CashFlow(maxLoanDuration, cashFlowForNewLongTermLoans).presentValue(riskParameters.longTermInterestRate); 
    }
    
    public double calculateMinPaybackyears(Client client, LoanRequest loanRequest, FreeCashFlowCalculator freeCashFlowCalculator, boolean refinanceExistingLongTermLoans) {
        
        double idealShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        
        double cashFlowForNewLongTermLoans;
        
        if(loanRequest.shortTermLoan >= idealShortTermLoan) {
            double amountAboveJustifiableSTLoan = loanRequest.shortTermLoan - idealShortTermLoan;
            // logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(client.industry);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            // logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(idealShortTermLoan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(loanRequest.shortTermLoan));
        }
        
        if(!refinanceExistingLongTermLoans) {
            double yealyDebtServiceForExistingLoans = client.existingLoans.yealyDebtService(riskParameters.longTermInterestRate, currentDate);
            cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yealyDebtServiceForExistingLoans);
        }
        
        return -ExcelFunctions.nper(riskParameters.longTermInterestRate.value, cashFlowForNewLongTermLoans, loanRequest.longTermLoan, 0, false);
        
    }
    
}
