package hu.lae.domain.riskparameters;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class OwnEquityRatioThresholdsTest {

    private final OwnEquityRatioThresholds ownEquityRatioThresholds = new OwnEquityRatioThresholds(0.7, 3, 0.5, 1);
    
    private final double industryAverage = 0.5;
    
    @Test
    public void maxLoanDuration() {
        
        Assert.assertEquals(Optional.of(1), ownEquityRatioThresholds.maxLoanDuration(0.1, industryAverage));
        Assert.assertEquals(Optional.of(1), ownEquityRatioThresholds.maxLoanDuration(0.2, industryAverage));
        Assert.assertEquals(Optional.of(3), ownEquityRatioThresholds.maxLoanDuration(0.3, industryAverage));
        Assert.assertEquals(Optional.empty(), ownEquityRatioThresholds.maxLoanDuration(0.4, industryAverage));
        Assert.assertEquals(Optional.empty(), ownEquityRatioThresholds.maxLoanDuration(0.5, industryAverage));
    }
    
    @Test
    public void minEquityRatio() {
        
        Assert.assertEquals(Optional.of(0.25), ownEquityRatioThresholds.minEquityRatio(industryAverage, 2));
        Assert.assertEquals(Optional.of(0.25), ownEquityRatioThresholds.minEquityRatio(industryAverage, 3));
        Assert.assertEquals(Optional.of(0.35), ownEquityRatioThresholds.minEquityRatio(industryAverage, 4));
        Assert.assertEquals(Optional.of(0.35), ownEquityRatioThresholds.minEquityRatio(industryAverage, 5));
    }
    
}
