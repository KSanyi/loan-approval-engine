package hu.lae.accounting;

import org.junit.Assert;
import org.junit.Test;

public class IncomeStatementTest {

    @Test
    public void test() {
        
        IncomeStatement incomeStatement = new IncomeStatement(2016, 1000, 100, 200);
        
        Double normalizedFreeCashFlow = incomeStatement.normalizedFreeCashFlow(0.1);
        
        Assert.assertEquals(790, normalizedFreeCashFlow.intValue());
        
    }
    
}
