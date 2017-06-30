package hu.lae.domain.loan;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.loan.ExistingLoans;
import hu.lae.domain.riskparameters.InterestRate;

public class ExistingLoanTest {
    
    @Test
    public void shortTermLoan() {
        long shortTermLoans = 50;
        long longTermLoans = 0;
        LocalDate expiry = LocalDate.of(2019, 6, 30);
        long bullet = 0;
        ExistingLoans existingLoans = new ExistingLoans(shortTermLoans, longTermLoans, expiry, bullet);
        
        double yealyDebtService = existingLoans.calculateYearlyDebtServiceForShortTermLoans(new InterestRate(0.03));
        
        Assert.assertEquals(1.5, yealyDebtService, 0.1);
    }
    
    @Test
    public void longTermLoan() {
        long shortTermLoans = 0;
        long longTermLoans = 100;
        LocalDate expiry = LocalDate.of(2019, 6, 30);
        long bullet = 0;
        ExistingLoans existingLoans = new ExistingLoans(shortTermLoans, longTermLoans, expiry, bullet);
        
        double yealyDebtService = existingLoans.calculateYearlyDebtServiceForLongTermLoans(new InterestRate(0.05), LocalDate.of(2017, 4, 9));
        
        Assert.assertEquals(56.17, yealyDebtService, 0.1);
    }
    
}
