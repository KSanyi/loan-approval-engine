package hu.lae.domain.finance.ebitdacorrection;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.finance.YearlyData;
import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;

public class EbitdaCorrectorTest {

    @Test
    public void baseTest() {
        EbitdaCorrectionParameters parameters = new EbitdaCorrectionParameters(0.3, 0.3, -0.1, -0.05, new YearlyData<>(0.334, 0.333, 0.333));
        EbitdaCorrector ebitdaCorrector = new EbitdaCorrector(parameters);
        
        YearlyData<EbitdaCorrectionInput> input = new YearlyData<>(
                new EbitdaCorrectionInput(2000, 270, 73),
                new EbitdaCorrectionInput(1800, 240, 60.83),
                new EbitdaCorrectionInput(1600, 235, 57.03));
        
        CorrectedEbitdas correctedEbitdas = ebitdaCorrector.xxx(input);
        
        Assert.assertEquals(244.5, correctedEbitdas.correctedEbitdaAverage, 0.1);
        Assert.assertEquals(261.0, correctedEbitdas.correctedLastEbitda, 0.1);
        Assert.assertEquals(248.0, correctedEbitdas.correctedEbitdaAverageWithoutTMinus1, 0.1);
        Assert.assertEquals(249.2, correctedEbitdas.correctedEbitdaAverageWithoutTMinus2, 0.1);
    }
    
    @Test
    public void testWithLowTSales() {
        EbitdaCorrectionParameters parameters = new EbitdaCorrectionParameters(0.3, 0.3, -0.1, -0.05, new YearlyData<>(0.334, 0.333, 0.333));
        EbitdaCorrector ebitdaCorrector = new EbitdaCorrector(parameters);
        
        YearlyData<EbitdaCorrectionInput> input = new YearlyData<>(
                new EbitdaCorrectionInput(1000, 270, 146),
                new EbitdaCorrectionInput(1800, 240, 60.83),
                new EbitdaCorrectionInput(1600, 235, 57.03));
        
        CorrectedEbitdas correctedEbitdas = ebitdaCorrector.xxx(input);
        
        Assert.assertEquals(139.1, correctedEbitdas.correctedEbitdaAverage, 0.1);
        Assert.assertEquals(207.1, correctedEbitdas.correctedLastEbitda, 0.1);
        Assert.assertEquals(139.1, correctedEbitdas.correctedEbitdaAverageWithoutTMinus1, 0.1);
        Assert.assertEquals(139.1, correctedEbitdas.correctedEbitdaAverageWithoutTMinus2, 0.1);
    }
    
    @Test
    public void testWithLowTMinus1Sales() {
        EbitdaCorrectionParameters parameters = new EbitdaCorrectionParameters(0.3, 0.3, -0.1, -0.05, new YearlyData<>(0.334, 0.333, 0.333));
        EbitdaCorrector ebitdaCorrector = new EbitdaCorrector(parameters);
        
        YearlyData<EbitdaCorrectionInput> input = new YearlyData<>(
                new EbitdaCorrectionInput(2000, 270, 73),
                new EbitdaCorrectionInput(1000, 240, 109.5),
                new EbitdaCorrectionInput(1600, 235, 57.03));
        
        CorrectedEbitdas correctedEbitdas = ebitdaCorrector.xxx(input);
        
        Assert.assertEquals(232.0, correctedEbitdas.correctedEbitdaAverage, 0.1);
        Assert.assertEquals(270.0, correctedEbitdas.correctedLastEbitda, 0.1);
        Assert.assertEquals(252.5, correctedEbitdas.correctedEbitdaAverageWithoutTMinus1, 0.1);
        Assert.assertEquals(230.5, correctedEbitdas.correctedEbitdaAverageWithoutTMinus2, 0.1);
    }
    
    @Test
    public void testWithLowTMinus2Sales() {
        EbitdaCorrectionParameters parameters = new EbitdaCorrectionParameters(0.3, 0.3, -0.1, -0.05, new YearlyData<>(0.334, 0.333, 0.333));
        EbitdaCorrector ebitdaCorrector = new EbitdaCorrector(parameters);
        
        YearlyData<EbitdaCorrectionInput> input = new YearlyData<>(
                new EbitdaCorrectionInput(2000, 270, 73),
                new EbitdaCorrectionInput(1800, 240, 60.83),
                new EbitdaCorrectionInput(1000, 235, 91.25));
        
        CorrectedEbitdas correctedEbitdas = ebitdaCorrector.xxx(input);
        
        Assert.assertEquals(245.3, correctedEbitdas.correctedEbitdaAverage, 0.1);
        Assert.assertEquals(260.9, correctedEbitdas.correctedLastEbitda, 0.1);
        Assert.assertEquals(248.0, correctedEbitdas.correctedEbitdaAverageWithoutTMinus1, 0.1);
        Assert.assertEquals(250.5, correctedEbitdas.correctedEbitdaAverageWithoutTMinus2, 0.1);
    }
    
}
