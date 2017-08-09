package hu.lae.domain.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.util.ExcelFunctions;

public class ExistingLoans {
    
    public static ExistingLoans createEmpty() {
        return new ExistingLoans(Collections.emptyList());
    }

    public final List<ExistingLoan> existingLoans;
    
    public ExistingLoans(List<ExistingLoan> existingLoans) {
        this.existingLoans = Collections.unmodifiableList(existingLoans);
    }
    
    public double calculateYearlyDebtServiceForLongTermLoans(InterestRate longTermInterestRate, LocalDate currentDate) {
        
        return existingLoans.stream()
            .filter(ExistingLoan::isLongTemLoan)
            .mapToDouble(l -> {
                double quartersUntilMaturity = ChronoUnit.DAYS.between(currentDate, l.expiry.get()) / 90.0;
                return -ExcelFunctions.pmt(longTermInterestRate.value, quartersUntilMaturity, l.amount, 0, 0) * 4;
            })
            .sum();
    }
    
    public double calculateYearlyDebtServiceForShortTermLoans(InterestRate shortTermInterestRate) {
        return shortTermInterestRate.multiply(shortTermLoansSum());
    }
    
    public long shortTermLoansSum() {
        return existingLoans.stream().filter(ExistingLoan::isShortTemLoan).mapToLong(l -> l.amount).sum();
    }
    
    public long longTermLoansSum() {
        return existingLoans.stream().filter(ExistingLoan::isLongTemLoan).mapToLong(l -> l.amount).sum();
    }
    
    public boolean isValid(LocalDate date) {
        return existingLoans.stream().filter(ExistingLoan::isLongTemLoan).allMatch(l -> !l.expiry.get().isBefore(date.plusYears(1)));
    }
    
    public List<Loan> toLoans() {
        return existingLoans.stream().map(ExistingLoan::toLoan).collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "\n" + existingLoans.stream().map(ExistingLoan::toString).collect(Collectors.joining("\n")) + "\n";
    }
    
}
