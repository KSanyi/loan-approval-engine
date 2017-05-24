package hu.lae.domain.accounting;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.accounting.IncomeStatement;
import hu.lae.domain.accounting.IncomeStatementData;

public class FreeCashFlowCalculatorTest {

    IncomeStatementData incomeStatementData = new IncomeStatementData(Arrays.asList(
            new IncomeStatement(2014, 1600, 800, 80, 200, 300), 
            new IncomeStatement(2015, 1800, 900, 110, 180, 300), 
            new IncomeStatement(2016, 2000, 1000, 100, 200, 300)));
    
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
