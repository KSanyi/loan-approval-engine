package hu.lae.riskparameters;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RiskParameters {

    public final String id;
    
    public final String name;
    
    public final BigDecimal amortizationRate;
    
    public final Haircuts haircuts;
    
    public final InterestRate shortTermInterestRate;
    
    public final InterestRate longTermInterestRate;
    
    public final BigDecimal dscrThreshold;

    public RiskParameters(String id, String name, BigDecimal amortizationRate, Haircuts haircuts, InterestRate shortTermInterestRate,
            InterestRate longTermInterestRate, BigDecimal dscrThreshold) {
        this.id = id;
        this.name = name;
        this.amortizationRate = amortizationRate;
        this.haircuts = haircuts;
        this.shortTermInterestRate = shortTermInterestRate;
        this.longTermInterestRate = longTermInterestRate;
        this.dscrThreshold = dscrThreshold;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
