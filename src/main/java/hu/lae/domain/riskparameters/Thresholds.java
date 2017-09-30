package hu.lae.domain.riskparameters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Thresholds {

    public final double equityRatio;
    
    public final double liquidityRatio;
    
    public final double turnoverRequirement;
    
    public final double debtCapacity;
    
    public final double localLoanRatio;
    
    public final OwnEquityRatioThresholds ownEquityRatioThresholds;
    
    public Thresholds(double equityRatio, double liquidityRatio, double turnoverReqTolerance, double debtCapacity, double localLoanRatio,
    		OwnEquityRatioThresholds ownEquityRatioThresholds) {
        this.equityRatio = equityRatio;
        this.liquidityRatio = liquidityRatio;
        this.turnoverRequirement = turnoverReqTolerance;
        this.debtCapacity = debtCapacity;
        this.localLoanRatio = localLoanRatio;
        this.ownEquityRatioThresholds = ownEquityRatioThresholds;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
