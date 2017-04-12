package hu.lae.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.riskparameters.InterestRate;
import hu.lae.util.ExcelFunctions;

public class ExistingLoans {
    
    public static ExistingLoans createEmpty() {
        return new ExistingLoans(0, 0, LocalDate.of(2018, 1, 1), 0, false);
    }

    public final long shortTermLoans;
    
    public final long longTermLoans;
    
    public final LocalDate expirity;
    
    public final long bullet;
    
    public final boolean isToBeRefinanced;

    public ExistingLoans(long shortTermLoans, long longTermLoans, LocalDate expirity, long bullet, boolean isToBeRefinanced) {
        this.shortTermLoans = shortTermLoans;
        this.longTermLoans = longTermLoans;
        this.expirity = expirity;
        this.bullet = bullet;
        this.isToBeRefinanced = isToBeRefinanced;
    }
    
    public double yealyDebtService(InterestRate longTermInterestRate, LocalDate currentDate) {
        double quartersUntilMaturity = ChronoUnit.DAYS.between(currentDate, expirity) / 90.0;
        return -ExcelFunctions.pmt(longTermInterestRate.value, quartersUntilMaturity, longTermLoans, bullet, 0) * 4;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
