package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.riskparameters.InterestRate;

public class ExistingLoanTest {
    
    @Test
    public void shortTermLoan() {
        ExistingLoans existingLoans = new ExistingLoans(Arrays.asList(ExistingLoan.newShortTermLoan(50, false)));
        
        double yealyDebtService = existingLoans.calculateYearlyDebtServiceForShortTermLoans(new InterestRate(0.03));
        
        Assert.assertEquals(1.5, yealyDebtService, 0.1);
    }
    
    @Test
    public void longTermLoan() {
        ExistingLoans existingLoans = new ExistingLoans(Arrays.asList(ExistingLoan.newLongTermLoan(100, LocalDate.of(2019, 6, 30), false)));
        
        double yealyDebtService = existingLoans.calculateYearlyDebtServiceForLongTermLoans(new InterestRate(0.05), LocalDate.of(2017, 4, 9));
        
        Assert.assertEquals(56.17, yealyDebtService, 0.1);
    }
    
}
