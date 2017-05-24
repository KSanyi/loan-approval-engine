package hu.lae.domain.riskparameters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Thresholds {

    public final double equityRatio;
    
    public final double liquidityRatio;

    public Thresholds(double equityRatio, double liquidityRatio) {
        this.equityRatio = equityRatio;
        this.liquidityRatio = liquidityRatio;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
