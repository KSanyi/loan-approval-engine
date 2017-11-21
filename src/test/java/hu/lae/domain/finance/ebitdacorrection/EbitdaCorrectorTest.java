package hu.lae.domain.finance.ebitdacorrection;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.finance.YearlyData;
import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;

public class EbitdaCorrectorTest {

    @Test
    public void test() {
        EbitdaCorrectionParameters parameters = new EbitdaCorrectionParameters(0.3, 0.1, -0.1, -0.05);
        EbitdaCorrector ebitdaCorrector = new EbitdaCorrector(parameters);
        
        YearlyData<EbitdaCorrectionInput> input = new YearlyData<>(
                new EbitdaCorrectionInput(2000, 370, 73),
                new EbitdaCorrectionInput(1800, 290, 60.83),
                new EbitdaCorrectionInput(1600, 235, 57.03));
        
        CorrectedEbitdas correctedEbitdas = ebitdaCorrector.xxx(input);
        
        System.out.println(correctedEbitdas);
        
        Assert.assertEquals(new CorrectedEbitdas(269.71, 357.67), correctedEbitdas);
    }
    
}
