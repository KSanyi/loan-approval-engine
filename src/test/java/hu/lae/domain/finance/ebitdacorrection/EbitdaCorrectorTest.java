package hu.lae.domain.finance.ebitdacorrection;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.finance.YearlyData;

public class EbitdaCorrectorTest {

    @Test
    public void test() {
        
        EbitdaCorrector ebitdaCorrector = new EbitdaCorrector(0.3, 0.1, -0.1);
        
        YearlyData<EbitdaCorrectionInput> input = new YearlyData<>(
                new EbitdaCorrectionInput(2000, 370, 73),
                new EbitdaCorrectionInput(1800, 290, 60.83),
                new EbitdaCorrectionInput(1600, 235, 57.03));
        
        YearlyData<Double> correctedEbitdas = ebitdaCorrector.xxx(input);
        
        Assert.assertEquals(new YearlyData<>(358L, 287L, 235L), correctedEbitdas);
    }
    
}
