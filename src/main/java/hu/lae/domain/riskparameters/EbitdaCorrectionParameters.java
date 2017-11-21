package hu.lae.domain.riskparameters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EbitdaCorrectionParameters {

    public final double reasonableEbitdaMarginGrowth;
    
    public final double maxDelta;
    
    public final double minXXX;
    
    public final double maxEbitdaDecrease;

    public EbitdaCorrectionParameters(double reasonableEbitdaMarginGrowth, double maxDelta, double minXXX, double maxEbitdaDecrease) {
        this.reasonableEbitdaMarginGrowth = reasonableEbitdaMarginGrowth;
        this.maxDelta = maxDelta;
        this.minXXX = minXXX;
        this.maxEbitdaDecrease = maxEbitdaDecrease;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
