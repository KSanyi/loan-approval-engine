package hu.lae.domain.loan;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.Client;
import hu.lae.domain.finance.CashFlow;
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.util.ExcelFunctions;

public class LoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final RiskParameters riskParameters;
    
    private final LocalDate currentDate;
    
    public final int maxLoanDuration;

    public LoanCalculator(RiskParameters riskParameters, LocalDate currentDate, int maxLoanDuration) {
        this.riskParameters = riskParameters;
        this.currentDate = currentDate;
        this.maxLoanDuration = maxLoanDuration;
    }
    
    public LoanRequest calculateIdealLoanRequest(Client client, FreeCashFlowCalculator freeCashFlowCalculator) {
        
        ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(client.existingLoans, true);
        
        double idealShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        
        double idealLongTermLoan = calculateMaxLongTermLoan(client,  idealShortTermLoan, maxLoanDuration, existingLoansRefinancing, freeCashFlowCalculator);
        
        LocalDate longTermLoanDuration = currentDate.plusYears(maxLoanDuration);
        LoanRequest idealLoanRequest = new LoanRequest(idealShortTermLoan, idealLongTermLoan, currentDate, longTermLoanDuration);
        
        return idealLoanRequest;
    }
    
    public double calculateMaxLongTermLoan(Client client, double shortTermLoanAmount, double paybackYears, ExistingLoansRefinancing existingLoansRefinancing, FreeCashFlowCalculator freeCashFlowCalculator) {
        
        logger.debug("------------------------- Max long term loan calculation -------------------------");
        logger.debug("Short term loan: " + shortTermLoanAmount + " paybackYears: " + paybackYears + " FreeCashFlowCalculator: " + freeCashFlowCalculator);
        logger.debug(existingLoansRefinancing.toString());
        
        double justifiableShortTermLoan = client.calculateJustifiableShortTermLoan(riskParameters.haircuts);
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        
        double cashFlowForNewLongTermLoans;
        
        double allShortTermLoans = shortTermLoanAmount + existingLoansRefinancing.nonRefinancableShortTermLoans();
        
        if(allShortTermLoans >= justifiableShortTermLoan) {
            double amountAboveJustifiableSTLoan = allShortTermLoans - justifiableShortTermLoan;
            // logger.info("Amount above justifiable ST loan: " + amountAboveJustifiableSTLoan);
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.interestRates.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            // logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.interestRates.shortTermInterestRate.multiply(justifiableShortTermLoan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.interestRates.shortTermInterestRate.multiply(allShortTermLoans));
        }
        
        // logger.info("Cash flow remaining for long term loans: " + cashFlowForNewLongTermLoans);
        
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.interestRates, currentDate);
        cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yearlyDebtServiceForExistingLongTermLoans);
        
        double maxLongTermLoan = new CashFlow((int)Math.round(paybackYears), cashFlowForNewLongTermLoans).presentValue(riskParameters.interestRates.longTermInterestRate);
        
        logger.debug("Calculated max long term loan: " + maxLongTermLoan);
        
        return maxLongTermLoan;
    }
    
    public double calculateMaxShortTermLoan(Client client, double longTermLoan, double paybackYears, ExistingLoansRefinancing existingLoansRefinancing, FreeCashFlowCalculator freeCashFlowCalculator) {
        
        logger.debug("------------------------- Max short term loan calculation -------------------------");
        logger.debug("Long term loan: " + longTermLoan + " paybackYears: " + paybackYears + " FreeCashFlowCalculator: " + freeCashFlowCalculator);
        logger.debug(existingLoansRefinancing.toString());
        
        LoanRequest idealLoanRequest = calculateIdealLoanRequest(client, freeCashFlowCalculator);
        
        double yearlyDebtServiceForExistingShortTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForShortTermLoans(riskParameters.interestRates, idealLoanRequest);
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.interestRates, currentDate);
        double yearlyDebtServiceForNewLongTermLoans = -ExcelFunctions.pmt(riskParameters.interestRates.longTermInterestRate.value, paybackYears, longTermLoan);
        
        double freeCashFlow = freeCashFlowCalculator.calculate(client.incomeStatementHistory(), riskParameters.amortizationRate);
        double remaining = Math.max(freeCashFlow / riskParameters.dscrThreshold - (yearlyDebtServiceForExistingShortTermLoans + yearlyDebtServiceForExistingLongTermLoans + yearlyDebtServiceForNewLongTermLoans), 0);
        
        double maxShortTermLoan;
        if(idealLoanRequest.shortTermLoan > existingLoansRefinancing.nonRefinancableShortTermLoans()) {
            double diff = idealLoanRequest.shortTermLoan - existingLoansRefinancing.nonRefinancableShortTermLoans();
            double interest = riskParameters.interestRates.shortTermInterestRate.multiply(diff);
            double xxx1 = (remaining - interest);
            double xxx2 = new CashFlow(maxLoanDuration, xxx1).presentValue(riskParameters.interestRates.longTermInterestRate);
            double xxx3 = remaining / riskParameters.interestRates.shortTermInterestRate.value;
            maxShortTermLoan = Math.min(diff, xxx3) + Math.max(0, xxx2);
        } else {
            maxShortTermLoan = new CashFlow(maxLoanDuration, remaining).presentValue(riskParameters.interestRates.longTermInterestRate);
        }
        
        logger.debug("Calculated max short term loan: " + maxShortTermLoan);
        
        return maxShortTermLoan;
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
            double cfNeededForStDebtService = -ExcelFunctions.pmt(riskParameters.interestRates.longTermInterestRate.value, maxLoanDuration, amountAboveJustifiableSTLoan);
            // logger.info("CF needed for above: " + cfNeededForStDebtService);
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.interestRates.shortTermInterestRate.multiply(idealShortTermLoan) - cfNeededForStDebtService);
        } else {
            cashFlowForNewLongTermLoans = Math.max(0, freeCashFlow / riskParameters.dscrThreshold - riskParameters.interestRates.shortTermInterestRate.multiply(allShortTermLoans));
        }
        
        double yearlyDebtServiceForExistingLongTermLoans = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(riskParameters.interestRates, currentDate);
        cashFlowForNewLongTermLoans = Math.max(0, cashFlowForNewLongTermLoans - yearlyDebtServiceForExistingLongTermLoans);
        
        double years = ExcelFunctions.nper(riskParameters.interestRates.longTermInterestRate.value, cashFlowForNewLongTermLoans, -loanRequest.longTermLoan, 0, false);
        
        logger.debug("Calculated min payback years: " + years);
        
        return years;
    }
    
}
