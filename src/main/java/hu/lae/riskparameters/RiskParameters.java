package hu.lae.riskparameters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RiskParameters {

    public final String id;
    
    public final String name;
    
    public final double amortizationRate;
    
    public final Haircuts haircuts;
    
    public final InterestRate shortTermInterestRate;
    
    public final int maxLoanDuration;
    
    public final InterestRate longTermInterestRate;
    
    public final double dscrThreshold;

    public RiskParameters(String id, String name, double amortizationRate, Haircuts haircuts, InterestRate shortTermInterestRate,
            int maxLoanDuration, InterestRate longTermInterestRate, double dscrThreshold) {
        this.id = id;
        this.name = name;
        this.amortizationRate = amortizationRate;
        this.haircuts = haircuts;
        this.shortTermInterestRate = shortTermInterestRate;
        this.maxLoanDuration = maxLoanDuration;
        this.longTermInterestRate = longTermInterestRate;
        this.dscrThreshold = dscrThreshold;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
