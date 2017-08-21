package hu.lae.domain.loan;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.Client;
import hu.lae.domain.accounting.CashFlow;
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.riskparameters.Industry;
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
        
        double idealLongTermLoan = calculateMaxLongTermLoan(client,  idealShortTermLoan, maxLoanDuration, existingLoansRefinancing, freeCashFlowCalculator);
        
        LoanRequest idealLoanRequest = new LoanRequest(idealShortTermLoan, idealLongTermLoan, maxLoanDuration);
        
        logger.info("Ideal loan request: " + idealLoanRequest);
        
        return idealLoanRequest;
    }
    
    public double calculateMaxLongTermLoan(Client client, double shortTermLoanAmount, int paybackYears, ExistingLoansRefinancing existingLoansRefinancing, FreeCashFlowCalculator freeCashFlowCalculator) {
        
        double justifiableShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        
        double cashFlowForNewLongTermLoans;
        
        double allShortTermLoans = shortTermLoanAmount + existingLoansRefinancing.nonRefinancableShortTermLoans();
        
        if(allShortTermLoans >= justifiableShortTermLoan) {
            double amountAboveJustifiableSTLoan = allShortTermLoans - justifiableShortTermLoan;
            // logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(client.industry);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            // logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(justifiableShortTermLoan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.shortTermInterestRate.multiply(allShortTermLoans));
        }
        
        // logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, currentDate);
        cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yearlyDebtServiceForExistingLongTermLoans);
        
        return new CashFlow(paybackYears, cashFlowForNewLongTermLoans).presentValue(riskParameters.longTermInterestRate); 
    }
    
    public double calculateMaxShortTermLoan(Client client, double longTermLoan, int paybackYears, ExistingLoansRefinancing existingLoansRefinancing, FreeCashFlowCalculator freeCashFlowCalculator) {
        
        LoanRequest idealLoanRequest = calculateIdealLoanRequest(client, freeCashFlowCalculator);
        
        double yearlyDebtServiceForExistingShortTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForShortTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, idealLoanRequest);
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.shortTermInterestRate, riskParameters.longTermInterestRate, currentDate);
        double yearlyDebtServiceForNewLongTermLoans = -ExcelFunctions.pmt(riskParameters.longTermInterestRate.value, paybackYears, longTermLoan);
        
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        double remaining = freeCashFlow - (yearlyDebtServiceForExistingShortTermLoans + yearlyDebtServiceForExistingLongTermLoans + yearlyDebtServiceForNewLongTermLoans);
        
        int maxLoanDuration = riskParameters.maxLoanDurations.maxLoanDuration(client.industry);
        if(idealLoanRequest.shortTermLoan > existingLoansRefinancing.nonRefinancableShortTermLoans()) {
            double diff = idealLoanRequest.shortTermLoan - existingLoansRefinancing.nonRefinancableShortTermLoans();
            double interest = riskParameters.shortTermInterestRate.multiply(diff);
            double xxx1 = remaining - interest;
            double xxx2 = new CashFlow(maxLoanDuration, xxx1).presentValue(riskParameters.longTermInterestRate);
            double xxx3 = remaining / riskParameters.shortTermInterestRate.value;
            return Math.min(diff, xxx3) + Math.max(0, xxx2);
        } else {
            return new CashFlow(maxLoanDuration, remaining).presentValue(riskParameters.longTermInterestRate);
        }
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
        
        logger.debug("Calculated min payback years: " + years);
        
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
