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
        
        ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(client.existingLoans, true);
        
        double idealShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        double idealLongTermLoan = calculateMaxLongTermLoan(maxLoanDuration, idealShortTermLoan, idealShortTermLoan, freeCashFlow, client.industry, existingLoansRefinancing);
        
        LoanRequest idealLoanRequest = new LoanRequest(idealShortTermLoan, idealLongTermLoan, maxLoanDuration);
        
        logger.info("Ideal loan request: " + idealLoanRequest);
        
        return idealLoanRequest;
    }
    
    public LoanApplicationResult calculate(Client client, LoanRequest loanRequest, FreeCashFlowCalculator freeCashFlowCalculator, ExistingLoansRefinancing existingLoansRefinancing) {
        
        logger.debug("------------------------- Loan calculation starts -------------------------");
        logger.debug(existingLoansRefinancing.toString());
        // logger.debug(client.toString());
        // logger.debug("Payback years: " + paybackYears + ", short term loan: " + shortTermLoan + ", FreeCashFlowCalculator: " + freeCashFlowCalculator + ", refinanceExistingLongTermLoans: " + refinanceExistingLongTermLoans);
        
        double justifiableShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        // logger.debug("Justifiable short term loan: " + justifiableShortTermLoan);

        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        // logger.debug("Normalized free cash flow: " + freeCashFlow);
        
        InterestRate shortTermInterestRate = riskParameters.shortTermInterestRate;
        
        double effectiveJustifiableSTLoan = Math.min(justifiableShortTermLoan, freeCashFlow / shortTermInterestRate.multiply(riskParameters.dscrThreshold));
        // logger.debug("Justifiable short term loan: " + effectiveJustifiableSTLoan + " = Min(" + justifiableShortTermLoan + ", " + freeCashFlow + " / (" + riskParameters.dscrThreshold + " * " + shortTermInterestRate + "))");

        LoanRequest idealLoanRequest = calculateIdealLoanRequest(client, freeCashFlowCalculator);
        
        double maxShortTermLoan = calculateMaxShortTermLoan(loanRequest, freeCashFlow, client.industry, existingLoansRefinancing, idealLoanRequest); 
        // logger.debug("Max short term loan: " + maxShortTermLoan + " = " + effectiveJustifiableSTLoan + " + maxLongTermLoan(" + paybackYears + ", " + effectiveJustifiableSTLoan + ")");
        
        double maxLongTermLoan = calculateMaxLongTermLoan(loanRequest.longTermLoanDuration, loanRequest.shortTermLoan, effectiveJustifiableSTLoan, freeCashFlow, client.industry, existingLoansRefinancing); 
        
        LoanApplicationResult result = new LoanApplicationResult(effectiveJustifiableSTLoan, maxShortTermLoan, maxLongTermLoan);
        
        logger.info("Calculation done: " + result);
        
        return result;
    }
    
    private double calculateMaxLongTermLoan(int paybackYears, double shortTermLoanAmount, double justifiableShortTermloan, double freeCashFlow, Industry industry, ExistingLoansRefinancing existingLoansRefinancing) {
        
        double cashFlowForNewLongTermLoans;
        
        double allShortTermLoans = shortTermLoanAmount + existingLoansRefinancing.nonRefinancableShortTermLoans();
        
        if(allShortTermLoans >= justifiableShortTermloan) {
            double amountAboveJustifiableSTLoan = allShortTermLoans - justifiableShortTermloan;
            // logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(industry);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            // logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermloan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(allShortTermLoans));
        }
        
        // logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, currentDate);
        cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yearlyDebtServiceForExistingLongTermLoans);
        
        return new CashFlow(paybackYears, cashFlowForNewLongTermLoans).presentValue(riskParameters.longTermInterestRate); 
    }
    
    private double calculateMaxShortTermLoan(LoanRequest loanRequest, double freeCashFlow, Industry industry, ExistingLoansRefinancing existingLoansRefinancing, LoanRequest idealLoanRequest) {
        
        double yearlyDebtServiceForExistingShortTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForShortTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, idealLoanRequest);
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, currentDate);
        double yearlyDebtServiceForNewLongTermLoans = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, loanRequest.longTermLoanDuration, loanRequest.longTermLoan);
        
        double remaining = freeCashFlow - (yearlyDebtServiceForExistingShortTermLoans + yearlyDebtServiceForExistingLongTermLoans + yearlyDebtServiceForNewLongTermLoans);
        
        return remaining;
    }
    
    private double calculateMaxShortTermLoanOld(double justifiableShortTermloan, double freeCashFlow, Industry industry, ExistingLoansRefinancing existingLoansRefinancing) {
        
        double cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermloan));
        
        // logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, currentDate);
        cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yearlyDebtServiceForExistingLongTermLoans);
        
        int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(industry);
        return justifiableShortTermloan + new CashFlow(maxLoanDuration, cashFlowForNewLongTermLoans).presentValue(riskParameters.longTermInterestRate); 
    }
    
    public double calculateMinPaybackYears(Client client, LoanRequest loanRequest, FreeCashFlowCalculator freeCashFlowCalculator, ExistingLoansRefinancing existingLoansRefinancing) {
        
        logger.debug("------------------------- Min payback years calculation -------------------------");
        logger.debug(loanRequest.toString());
        logger.debug("FreeCashFlowCalculator: " + freeCashFlowCalculator);
        logger.debug(existingLoansRefinancing.toString());
        
        double idealShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        
        double cashFlowForNewLongTermLoans;
        
        double allShortTermLoans = loanRequest.shortTermLoan + existingLoansRefinancing.nonRefinancableShortTermLoans();
        
        if(allShortTermLoans >= idealShortTermLoan) {
            double amountAboveJustifiableSTLoan = allShortTermLoans - idealShortTermLoan;
            // logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(client.industry);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            // logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(idealShortTermLoan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(allShortTermLoans));
        }
        
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, currentDate);
        cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yearlyDebtServiceForExistingLongTermLoans);
        
        double years = ExcelFunctions.nper(riskParameters.longTermInterestRate.value, cashFlowForNewLongTermLoans, -loanRequest.longTermLoan, 0, false);
        
        logger.debug("Calculatyed min payback years: " + years);
        
        return years;
    }
    
    public double calculateDSCR(LoanRequest loanRequest, Client client, FreeCashFlowCalculator freeCashFlowCalculator) {
        
        logger.debug("------------------------- DSCR calculation -------------------------");
        logger.debug(loanRequest.toString());
        logger.debug("FreeCashFlowCalculator: " + freeCashFlowCalculator);
        
        double allShortTermLoan = client.existingLoans.shortTermLoansSum() + loanRequest.shortTermLoan;
        double interestForShortTermLoan = riskParameters.shortTermInterestRate.multiply(allShortTermLoan);
        
        double debtServiceForExistingLongTermLoan = client.existingLoans.calculateYearlyDebtServiceForLongTermLoans(riskParameters.longTermInterestRate, currentDate);
        
        double debtServiceForRequestedLongTermLoan = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, 5, loanRequest.longTermLoan);
        
        double fullDebtService = interestForShortTermLoan + debtServiceForExistingLongTermLoan + debtServiceForRequestedLongTermLoan;
        
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        
        double dscr = freeCashFlow / fullDebtService;
        
        logger.debug("Calculated DSCR: " + dscr);
        
        return dscr;
    }
    
}
