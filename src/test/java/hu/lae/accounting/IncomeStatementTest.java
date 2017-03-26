package hu.lae.accounting;

import org.junit.Assert;
import org.junit.Test;

public class IncomeStatementTest {

    @Test
    public void test() {
        
        IncomeStatement incomeStatement = new IncomeStatement(2016, 1000, 100, 200);
        
        double amortizationRate = 0.1;
        Double normalizedFreeCashFlow = incomeStatement.normalizedFreeCashFlow(amortizationRate);
        
        Assert.assertEquals(790, normalizedFreeCashFlow.intValue());
        
    }
    
}
