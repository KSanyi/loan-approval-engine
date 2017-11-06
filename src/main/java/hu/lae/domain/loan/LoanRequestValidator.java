package hu.lae.domain.loan;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.Client;
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.industry.IndustryData;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.util.MathUtil;

public class LoanRequestValidator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final LoanCalculator loanCalculator;
    
    private final LoanPreCalculator loanPreCalculator;
    
    private final IndustryData industryData;
    
    private final RiskParameters riskParameters;
    
    public LoanRequestValidator(LoanCalculator loanCalculator, LoanPreCalculator loanPreCalculator, IndustryData industryData) {
        this.loanCalculator = loanCalculator;
        this.loanPreCalculator = loanPreCalculator;
        this.industryData = industryData;
        this.riskParameters = loanCalculator.riskParameters;
    }

    public List<String> validate(FreeCashFlowCalculator freeCashFlowCalculator, Client client, ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest) {

        logger.info("Validating loan request");
        
        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, 0, loanRequest.maturityYears(), existingLoansRefinancing, freeCashFlowCalculator);
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, loanRequest.shortTermLoan, loanRequest.maturityYears(), existingLoansRefinancing, freeCashFlowCalculator);

        double loanIncrement = loanRequest.sum() - existingLoansRefinancing.sumOfRefinancableLoans();
        int maxLoanDuration = loanPreCalculator.calculateMaxLoanDuration(client, loanIncrement);

        List<String> errorMessages = new ArrayList<>();

        if (loanRequest.sum() < existingLoansRefinancing.sumOfRefinancableLoans()) {
            errorMessages.add("Loan request must be enough to cover the existing refinanceable loans");
        }

        if (loanRequest.shortTermLoan > maxShortTermLoan || loanRequest.longTermLoan > maxLongTermLoan) {
            if (loanRequest.shortTermLoan > maxShortTermLoan) {
                errorMessages.add("Short term loan must not exceed " + MathUtil.round(maxShortTermLoan, 1));
            } else if (loanRequest.longTermLoan > maxLongTermLoan) {
                errorMessages.add("Long term loan must not exceed " + MathUtil.round(maxLongTermLoan, 1));
            }
        } else if (maxLoanDuration < loanRequest.maturityYears()) {
        	double ownEquityRatioIndustryAverage = industryData.ownEquityRatioAverage(client.industry);
            Optional<Double> minEquityRatio = riskParameters.minOwnEquityRatio(ownEquityRatioIndustryAverage, loanRequest.maturityYears());
            Optional<Double> maxNewLoan = minEquityRatio.flatMap(ratio -> client.financialStatementData().balanceSheet.liabilities.maxNewLoanToEquityRatio(ratio));
            
            if(maxNewLoan.isPresent()) {
                errorMessages.add("Decrease loan amount to " + MathUtil.round(maxNewLoan.get(), 1));
            } else {
                logger.error("No max loan value found for {} years", loanRequest.maturityYears());
            }
        }
        
        if(!errorMessages.isEmpty()) {
            logger.info("Validation failure: {}", errorMessages);
        }

        return errorMessages;
    }

}
