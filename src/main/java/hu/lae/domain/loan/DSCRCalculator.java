package hu.lae.domain.loan;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.util.ExcelFunctions;

public class DSCRCalculator {
																																																																																	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public static double calculateDSCR(InterestRates interestRates, double freeCashFlow, LoanRequest loanRequest, ExistingLoansRefinancing existingLoansRefinancing, LocalDate date) {
        
        logger.debug("------------------------- DSCR calculation -------------------------");
        logger.debug(loanRequest.toString());
        
        double allShortTermLoan = existingLoansRefinancing.nonRefinancableShortTermLoans() + loanRequest.shortTermLoan;
        double interestForShortTermLoan = interestRates.shortTermInterestRate.multiply(allShortTermLoan);
        
        double debtServiceForExistingLongTermLoan = existingLoansRefinancing.calculateYearlyDebtServiceForLongTermLoans(interestRates, date);
        
        double debtServiceForRequestedLongTermLoan = -ExcelFunctions.pmt(interestRates.longTermInterestRate.value, loanRequest.maturityYears(), loanRequest.longTermLoan);
        
        double fullDebtService = interestForShortTermLoan + debtServiceForExistingLongTermLoan + debtServiceForRequestedLongTermLoan;
        
        double dscr = freeCashFlow / fullDebtService;
        
        logger.debug("Calculated DSCR: " + dscr);
        
        return dscr;
    }
	
}
