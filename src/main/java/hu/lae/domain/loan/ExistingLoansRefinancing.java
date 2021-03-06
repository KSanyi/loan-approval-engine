package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.util.ExcelFunctions;

public class ExistingLoansRefinancing {

    public final Map<ExistingLoan, Boolean> existingLoansRefinancingMap;

    public ExistingLoansRefinancing(Map<ExistingLoan, Boolean> existingLoansRefinancingMap) {
        this.existingLoansRefinancingMap = existingLoansRefinancingMap;
    }
    
    public ExistingLoansRefinancing(ExistingLoans existingLoans, boolean refinance) {
        this.existingLoansRefinancingMap = existingLoans.existingLoans.stream().collect(Collectors.toMap(Function.identity(), e -> refinance));
    }
    
    private boolean refinance(ExistingLoan existingLoan) {
        return existingLoansRefinancingMap.get(existingLoan);
    }
    
    public double calculateYearlyDebtServiceForShortTermLoans(InterestRates interestRates, LoanRequest idealLoanRequest) {
        double nonRefinancableShortTermLoans = nonRefinancableShortTermLoans();
        
        if(nonRefinancableShortTermLoans > idealLoanRequest.shortTermLoan) {
            double debtService = -ExcelFunctions.pmt(interestRates.longTermInterestRate.value, idealLoanRequest.maturityYears(), nonRefinancableShortTermLoans - idealLoanRequest.shortTermLoan);
            return interestRates.shortTermInterestRate.multiply(idealLoanRequest.shortTermLoan) + debtService;
        } else {
            return interestRates.shortTermInterestRate.multiply(nonRefinancableShortTermLoans);
        }
    }
    
    public double calculateYearlyDebtServiceForLongTermLoans(InterestRates interestRates, LocalDate currentDate) {
        double yearlyDebtServiceForExistingLoans = 0;
        
        for(ExistingLoan existingLoan : existingLoansRefinancingMap.keySet()) {
            if(existingLoan.isLongTemLoan() && !refinance(existingLoan)) {
                yearlyDebtServiceForExistingLoans += existingLoan.calculateYearlyDebtService(interestRates, currentDate);
            }
        }
        
        return yearlyDebtServiceForExistingLoans;
    }

    public List<ExistingLoan> nonRefinancableLoans() {
        return existingLoansRefinancingMap.keySet().stream()
            .filter(loan -> !refinance(loan))
            .collect(Collectors.toList());
    }
    
    public double sumOfNonRefinancableLoans() {
        return existingLoansRefinancingMap.keySet().stream()
            .filter(loan -> !refinance(loan))
            .mapToLong(l -> l.amount)
            .sum();
    }
    
    public long sumOfRefinancableLoans() {
        return existingLoansRefinancingMap.keySet().stream()
            .filter(this::refinance)
            .mapToLong(l -> l.amount)
            .sum();
    }
    
    public long localNonRefinancableLoans() {
        return existingLoansRefinancingMap.keySet().stream()
            .filter(loan -> !refinance(loan))
            .filter(loan -> loan.isOwn)
            .mapToLong(l -> l.amount)
            .sum();
    }

    public long nonRefinancableShortTermLoans() {
        return existingLoansRefinancingMap.keySet().stream()
                .filter(loan -> !refinance(loan))
                .filter(l -> l.isShortTemLoan())
                .mapToLong(l -> l.amount)
                .sum();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public List<Loan> toLoans() {
        return existingLoansRefinancingMap.keySet().stream().map(loan -> loan.toLoan(existingLoansRefinancingMap.get(loan))).collect(Collectors.toList());
    }
    
}
