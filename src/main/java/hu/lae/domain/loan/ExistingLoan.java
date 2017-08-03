package hu.lae.domain.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.util.ExcelFunctions;

public class ExistingLoan {

    public static ExistingLoan newShortTermLoan(long amount, boolean isLocal) {
        return new ExistingLoan(amount, Optional.empty(), isLocal, LoanType.ShortTerm);
    }
    
    public static ExistingLoan newLongTermLoan(long amount, LocalDate expiry, boolean isLocal) {
        return new ExistingLoan(amount, Optional.of(expiry), isLocal, LoanType.LongTerm);
    }
    
    public final long amount;
    
    public final Optional<LocalDate> expiry;
    
    public final boolean isLocal;
    
    public final LoanType type;

    private ExistingLoan(long amount, Optional<LocalDate> expiry, boolean isLocal, LoanType type) {
        this.amount = amount;
        this.expiry = expiry;
        this.isLocal = isLocal;
        this.type = type;
    }
    
    public boolean isShortTemLoan() {
        return type == LoanType.ShortTerm;
    }
    
    public boolean isLongTemLoan() {
        return type == LoanType.LongTerm;
    }
    
    public double calculateYearlyDebtService(InterestRate shortTermInterestRate, InterestRate longTermInterestRate, LocalDate currentDate) {
        if(isShortTemLoan()) {
            return shortTermInterestRate.multiply(amount);
        } else {
            double quartersUntilMaturity = ChronoUnit.DAYS.between(currentDate, expiry.get()) / 90.0;
            return -ExcelFunctions.pmt(longTermInterestRate.value, quartersUntilMaturity, amount, 0, 0) * 4;
        }
    }
    
    public Loan toLoan() {
        return new Loan(false, amount, type, isLocal);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
