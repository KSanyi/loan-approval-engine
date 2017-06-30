package hu.lae.domain.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.util.Clock;
import hu.lae.util.ExcelFunctions;

public class ExistingLoans {
    
    public static ExistingLoans createEmpty() {
        return new ExistingLoans(0, 0, Clock.date().plusYears(1), 0);
    }

    public final long shortTermLoans;
    
    public final long longTermLoans;
    
    public final LocalDate expiry;
    
    public final long bullet;
    
    public ExistingLoans(long shortTermLoans, long longTermLoans, LocalDate expiry, long bullet) {
        this.shortTermLoans = shortTermLoans;
        this.longTermLoans = longTermLoans;
        this.expiry = expiry;
        this.bullet = bullet;
    }
    
    public double calculateYearlyDebtServiceForLongTermLoans(InterestRate longTermInterestRate, LocalDate currentDate) {
        double quartersUntilMaturity = ChronoUnit.DAYS.between(currentDate, expiry) / 90.0;
        return -ExcelFunctions.pmt(longTermInterestRate.value, quartersUntilMaturity, longTermLoans, bullet, 0) * 4;
    }
    
    public double calculateYearlyDebtServiceForShortTermLoans(InterestRate shortTermInterestRate) {
        return shortTermInterestRate.multiply(shortTermLoans);
    }
    
    public boolean isValid(LocalDate date) {
        return longTermLoans == 0 || !expiry.isBefore(date.plusYears(1));
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public double sum(boolean refinanceLShortTermLoans, boolean refinanceLongTermLoans) {
        return (refinanceLShortTermLoans ? 0: shortTermLoans) + (refinanceLongTermLoans ? 0 : longTermLoans);
    }
    
}
