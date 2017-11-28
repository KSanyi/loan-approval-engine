package hu.lae.domain.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.util.ExcelFunctions;

public class ExistingLoan {

    public static ExistingLoan newShortTermLoan(long amount, boolean isOwn) {
        return new ExistingLoan(amount, Optional.empty(), isOwn, LoanType.ShortTerm);
    }
    
    public static ExistingLoan newLongTermLoan(long amount, LocalDate expiry, boolean isOwn) {
        return new ExistingLoan(amount, Optional.of(expiry), isOwn, LoanType.LongTerm);
    }
    
    public final long amount;
    
    public final Optional<LocalDate> expiry;
    
    public final boolean isOwn;
    
    public final LoanType type;

    private ExistingLoan(long amount, Optional<LocalDate> expiry, boolean isOwn, LoanType type) {
        this.amount = amount;
        this.expiry = expiry;
        this.isOwn = isOwn;
        this.type = type;
    }
    
    public boolean isShortTemLoan() {
        return type == LoanType.ShortTerm;
    }
    
    public boolean isLongTemLoan() {
        return type == LoanType.LongTerm;
    }
    
    public double calculateYearlyDebtService(InterestRates interestRates, LocalDate currentDate) {
        if(isShortTemLoan()) {
            return interestRates.shortTermInterestRate.multiply(amount);
        } else {
            double quartersUntilMaturity = ChronoUnit.DAYS.between(currentDate, expiry.get()) / 90.0;
            return -ExcelFunctions.pmt(interestRates.longTermInterestRate.value, quartersUntilMaturity, amount) * 4;
        }
    }
    
    public Loan toLoan(boolean refinanced) {
        return new Loan(false, amount, type, isOwn, refinanced);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
