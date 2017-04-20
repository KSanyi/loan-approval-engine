package hu.lae.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.riskparameters.InterestRate;
import hu.lae.util.Clock;
import hu.lae.util.ExcelFunctions;

public class ExistingLoans {
    
    public static ExistingLoans createEmpty() {
        return new ExistingLoans(0, 0, Clock.date().plusYears(1), 0, false);
    }

    public final long shortTermLoans;
    
    public final long longTermLoans;
    
    public final LocalDate expiry;
    
    public final long bullet;
    
    public final boolean isToBeRefinanced;

    public ExistingLoans(long shortTermLoans, long longTermLoans, LocalDate expiry, long bullet, boolean isToBeRefinanced) {
        this.shortTermLoans = shortTermLoans;
        this.longTermLoans = longTermLoans;
        this.expiry = expiry;
        this.bullet = bullet;
        this.isToBeRefinanced = isToBeRefinanced;
    }
    
    public double yealyDebtService(InterestRate longTermInterestRate, LocalDate currentDate) {
        double quartersUntilMaturity = ChronoUnit.DAYS.between(currentDate, expiry) / 90.0;
        return -ExcelFunctions.pmt(longTermInterestRate.value, quartersUntilMaturity, longTermLoans, bullet, 0) * 4;
    }
    
    public boolean isValid(LocalDate date) {
        return longTermLoans == 0 || !expiry.isBefore(date.plusYears(1));
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
