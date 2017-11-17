package hu.lae.domain.finance.ebitdacorrection;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class EbitdaCorrectorTest {

    @Test
    public void test() {
        
        EbitdaCorrector ebitdaCorrector = new EbitdaCorrector(0.3);
        
        List<EbitdaCorretionInput> input = Arrays.asList(
                new EbitdaCorretionInput(2000, 370, 73),
                new EbitdaCorretionInput(1800, 290, 60.83),
                new EbitdaCorretionInput(1600, 235, 57.03));
        
        List<Long> correctedEbitdas = ebitdaCorrector.calculateCorrectedEbitdas(input);
        
        Assert.assertEquals(Arrays.asList(358L, 287L, 235L), correctedEbitdas);
    }
    
}
