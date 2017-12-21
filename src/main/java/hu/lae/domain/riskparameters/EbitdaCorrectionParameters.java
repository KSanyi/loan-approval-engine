package hu.lae.domain.riskparameters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.finance.YearlyData;

public class EbitdaCorrectionParameters {

    public final double reasonableEbitdaMarginGrowth;
    
    public final double maxDelta;
    
    public final double minXXX;
    
    public final double maxEbitdaDecrease;
    
    public final YearlyData<Double> yearlyWeights;

    public EbitdaCorrectionParameters(double reasonableEbitdaMarginGrowth, double maxDelta, double minXXX, double maxEbitdaDecrease, YearlyData<Double> yearlyWeights) {
        this.reasonableEbitdaMarginGrowth = reasonableEbitdaMarginGrowth;
        this.maxDelta = maxDelta;
        this.minXXX = minXXX;
        this.maxEbitdaDecrease = maxEbitdaDecrease;
        this.yearlyWeights = yearlyWeights;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
