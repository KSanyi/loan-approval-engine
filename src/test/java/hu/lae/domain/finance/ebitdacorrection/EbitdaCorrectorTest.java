package hu.lae.domain.finance.ebitdacorrection;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.finance.YearlyData;
import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;

public class EbitdaCorrectorTest {

    @Test
    public void test() {
        EbitdaCorrectionParameters parameters = new EbitdaCorrectionParameters(0.3, 0.3, -0.1, -0.05);
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
    
}
