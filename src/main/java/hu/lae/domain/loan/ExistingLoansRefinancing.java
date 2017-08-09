package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import hu.lae.domain.riskparameters.InterestRate;

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
    
    public double calculateYearlyDebtServiceForLongTermLoans(InterestRate shortTermInterestRate, InterestRate longTermInterestRate, LocalDate currentDate) {
        double yearlyDebtServiceForExistingLoans = 0;
        
        for(ExistingLoan existingLoan : existingLoansRefinancingMap.keySet()) {
            if(existingLoan.isLongTemLoan() && !refinance(existingLoan)) {
                yearlyDebtServiceForExistingLoans += existingLoan.calculateYearlyDebtService(shortTermInterestRate, longTermInterestRate, currentDate);
            }
        }
        
        return yearlyDebtServiceForExistingLoans;
    }

    public double nonRefinancableLoans() {
        return existingLoansRefinancingMap.keySet().stream()
            .filter(loan -> !refinance(loan))
            .mapToLong(l -> l.amount)
            .sum();
    }
    
    public double localNonRefinancableLoans() {
        return existingLoansRefinancingMap.keySet().stream()
            .filter(loan -> !refinance(loan))
            .filter(loan -> loan.isLocal)
            .mapToLong(l -> l.amount)
            .sum();
    }

    public double refinancableLongTermLoans() {
        return existingLoansRefinancingMap.keySet().stream()
                .filter(this::refinance)
                .filter(l -> l.isLongTemLoan())
                .mapToLong(l -> l.amount)
                .sum();
    }

    public double nonRefinancableShortTermLoans() {
        return existingLoansRefinancingMap.keySet().stream()
                .filter(loan -> !refinance(loan))
                .filter(l -> l.isShortTemLoan())
                .mapToLong(l -> l.amount)
                .sum();
    }
    
}
