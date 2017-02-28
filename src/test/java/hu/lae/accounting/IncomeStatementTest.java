package hu.lae.accounting;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

public class IncomeStatementTest {

    @Test
    public void test() {
        
        IncomeStatement incomeStatement = new IncomeStatement(2016, 1000, 100, 200);
        
        BigDecimal normalizedFreeCashFlow = incomeStatement.normalizedFreeCashFlow(new BigDecimal("0.1"));
        
        Assert.assertEquals(790, normalizedFreeCashFlow.intValue());
        
    }
    
}
