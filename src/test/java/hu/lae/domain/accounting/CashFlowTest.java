package hu.lae.domain.accounting;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.accounting.CashFlow;
import hu.lae.domain.riskparameters.InterestRate;

public class CashFlowTest {

    @Test
    public void test1() {
        CashFlow cashFlow = new CashFlow(Arrays.asList(100d, 120d, 130d));
        InterestRate rate = new InterestRate(0.05);
        
        Assert.assertEquals(316.38, cashFlow.presentValue(rate), 0.01);
    }
    
    @Test
    public void test2() {
        CashFlow cashFlow = new CashFlow(3, 100d);
        InterestRate rate = new InterestRate(0.05);
        
        Assert.assertEquals(272.32, cashFlow.presentValue(rate), 0.01);
    }
    
}
