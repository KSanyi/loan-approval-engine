package hu.lae.domain.riskparameters;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.industry.Industry;

public class RiskParameters {

    public final String id;
    
    public final String name;
    
    public final double amortizationRate;
    
    public final Haircuts haircuts;
    
    public final InterestRates interestRates;
    
    public final MaxLoanDurations maxLoanDurations;
    
    public final double dscrThreshold;
    
    public final Thresholds thresholds;
    
    public final CollateralRequirement collateralRequirement;
    
    public RiskParameters(String id, String name, double amortizationRate, Haircuts haircuts, InterestRates interestRates,
            MaxLoanDurations maxLoanDurations, double dscrThreshold, Thresholds thresholds,
            CollateralRequirement collateralRequirement) {
        this.id = id;
        this.name = name;
        this.amortizationRate = amortizationRate;
        this.haircuts = haircuts;
        this.interestRates = interestRates;
        this.maxLoanDurations = maxLoanDurations;
        this.dscrThreshold = dscrThreshold;
        this.thresholds = thresholds;
        this.collateralRequirement = collateralRequirement;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public int maxLoanDuration(Industry industry, double industryAverage, double ownEquityRatio) {
		int maxLoanDurationByIndustry = maxLoanDurations.maxLoanDuration(industry);
		Optional<Integer> maxLoanDurationCap = thresholds.ownEquityRatioThresholds.maxLoanDuration(ownEquityRatio, industryAverage);
		return maxLoanDurationCap.map(cap -> Math.min(cap, maxLoanDurationByIndustry)).orElse(maxLoanDurationByIndustry);
	}
    
}
