package hu.lae.domain.loan;

import java.util.ArrayList;
import java.util.List;

import hu.lae.domain.Client;
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.util.MathUtil;

public class LoanRequestValidator {

    private final LoanCalculator loanCalculator;
    
    private final RiskParameters riskParameters;
    
    public LoanRequestValidator(LoanCalculator loanCalculator) {
        this.loanCalculator = loanCalculator;
        this.riskParameters = loanCalculator.riskParameters;
    }

    public List<String> validate(FreeCashFlowCalculator freeCashFlowCalculator, Client client, ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest) {

        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, 0, loanRequest.longTermLoanDuration,
                existingLoansRefinancing, freeCashFlowCalculator);
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, loanRequest.shortTermLoan, loanRequest.longTermLoanDuration,
                existingLoansRefinancing, freeCashFlowCalculator);

        double ownEquityRatioAverage = loanCalculator.industryData.ownEquityRatioAverage(client.industry);
        double loanIncrement = loanRequest.sum() - existingLoansRefinancing.sumOfRefinancableLoans();
        int maxLoanDuration = riskParameters.maxLoanDuration(client.industry, ownEquityRatioAverage, client.financialStatementData().balanceSheet.liabilities.equityRatio(loanIncrement));

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
            errorMessages.add("Decrease loan amount or maturity shall be decreased to RP years");
        }

        return errorMessages;
    }

}
