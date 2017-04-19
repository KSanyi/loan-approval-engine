package hu.lae.accounting;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class FreeCashFlowCalculatorTest {

    IncomeStatementData incomeStatementData = new IncomeStatementData(Arrays.asList(
            new IncomeStatement(2014, 800, 80, 200), 
            new IncomeStatement(2015, 900, 110, 180), 
            new IncomeStatement(2016, 1000, 100, 200)));
    
    double amortizationRate = 0.1;
    
    @Test
    public void lastYearBasedCalculation() {
        Double normalizedFreeCashFlow = FreeCashFlowCalculator.lastYear.calculate(incomeStatementData, amortizationRate);
        
        Assert.assertEquals(790, normalizedFreeCashFlow.intValue());
    }
    
    @Test
    public void averageBasedCalculation() {
        Double normalizedFreeCashFlow = FreeCashFlowCalculator.average.calculate(incomeStatementData, amortizationRate);
        
        Assert.assertEquals(696, normalizedFreeCashFlow.intValue());
    }
    
}
