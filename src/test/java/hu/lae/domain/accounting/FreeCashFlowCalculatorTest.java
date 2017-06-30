package hu.lae.domain.accounting;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.util.MapFactory;

public class FreeCashFlowCalculatorTest {

    IncomeStatementHistory incomeStatementData = new IncomeStatementHistory(MapFactory.of(
            2014, new IncomeStatement(1600, 800, 80, 200, 300),
            2015, new IncomeStatement(1800, 900, 110, 180, 300),
            2016, new IncomeStatement(2000, 1000, 100, 200, 300)));
    
    double amortizationRate = 0.1;
    
    @Test
    public void lastYearBasedCalculation() {
        Double normalizedFreeCashFlow = FreeCashFlowCalculator.lastYear.calculate(incomeStatementData, amortizationRate);
        
        Assert.assertEquals(890, normalizedFreeCashFlow.intValue());
    }
    
    @Test
    public void averageBasedCalculation() {
        Double normalizedFreeCashFlow = FreeCashFlowCalculator.average.calculate(incomeStatementData, amortizationRate);
        
        Assert.assertEquals(793, normalizedFreeCashFlow.intValue());
    }
    
}
