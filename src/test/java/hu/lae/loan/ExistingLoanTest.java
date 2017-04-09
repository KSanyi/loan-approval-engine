package hu.lae.loan;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.riskparameters.InterestRate;

public class ExistingLoanTest {
    
    @Test
    public void test() {
        long shortTermLoans = 0;
        long longTermLoans = 100;
        LocalDate expirity = LocalDate.of(2019, 6, 30);
        long bullet = 0;
        boolean isToBeRefinanced = false;
        ExistingLoans existingLoans = new ExistingLoans(shortTermLoans, longTermLoans, expirity, bullet, isToBeRefinanced);
        
        double yealyDebtService = existingLoans.yealyDebtService(new InterestRate(0.05), LocalDate.of(2017, 4, 9));
        
        Assert.assertEquals(56.17, yealyDebtService, 0.1);
    }
    
}
