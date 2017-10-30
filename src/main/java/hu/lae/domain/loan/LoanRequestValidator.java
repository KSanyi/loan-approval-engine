package hu.lae.domain.loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hu.lae.domain.Client;
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.industry.IndustryData;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.util.MathUtil;

public class LoanRequestValidator {

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

        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, 0, loanRequest.longTermLoanDuration,
                existingLoansRefinancing, freeCashFlowCalculator);
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, loanRequest.shortTermLoan, loanRequest.longTermLoanDuration,
                existingLoansRefinancing, freeCashFlowCalculator);

        double loanIncrement = loanRequest.sum() - existingLoansRefinancing.sumOfRefinancableLoans();
        int maxLoanDuration = loanPreCalculator.calculateMaxLoanDuration(client, loanIncrement);

        List<String> errorMessages = new ArrayList<>();

        if (loanRequest.sum() < existingLoansRefinancing.sumOfRefinancableLoans()) {
            errorMessages.add("Loan request must be enough to cover the existing refinanceable loans");
        }

        if (loanRequest.shortTermLoan > maxShortTermLoan) {
            errorMessages.add("Short term loan must not exceed " + MathUtil.round(maxShortTermLoan, 1));
        } else if (loanRequest.longTermLoan > maxLongTermLoan) {
            errorMessages.add("Long term loan must not exceed " + MathUtil.round(maxLongTermLoan, 1));
        }
        if (maxLoanDuration < loanRequest.longTermLoanDuration) {
        	double ownEquityRatioIndustryAverage = industryData.ownEquityRatioAverage(client.industry);
            Optional<Double> minEquityRatio = riskParameters.minOwnEquityRatio(ownEquityRatioIndustryAverage, loanRequest.longTermLoanDuration);
            Optional<Double> maxNewLoan = minEquityRatio.flatMap(ratio -> client.financialStatementData().balanceSheet.liabilities.maxNewLoanToEquityRatio(ratio));
            
            if(maxNewLoan.isPresent()) {
                errorMessages.add("Decrease loan amount to " + MathUtil.round(maxNewLoan.get(), 1) + " or maturity to " + maxLoanDuration + " years");
            } else {
                errorMessages.add("Decrease maturity to " + maxLoanDuration + " years");
            }
        }

        return errorMessages;
    }

}
