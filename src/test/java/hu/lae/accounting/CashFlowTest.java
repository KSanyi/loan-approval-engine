package hu.lae.accounting;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.riskparameters.InterestRate;

public class CashFlowTest {

    @Test
    public void test1() {
        CashFlow cashFlow = new CashFlow(Arrays.asList(new BigDecimal(100), new BigDecimal(120), new BigDecimal(130)));
        InterestRate rate = new InterestRate(new BigDecimal("0.05"));
        
        Assert.assertEquals(316, cashFlow.presentValue(rate).intValue());
    }
    
    @Test
    public void test2() {
        CashFlow cashFlow = new CashFlow(3, 100);
        InterestRate rate = new InterestRate(new BigDecimal("0.05"));
        
        Assert.assertEquals(272, cashFlow.presentValue(rate).intValue());
    }
    
}
