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
    
    public final IndustryMaxLoanDurations industryMaxLoanDurations;
    
    public final double dscrThreshold;
    
    public final Thresholds thresholds;
    
    public final CollateralRequirement collateralRequirement;
    
    public final EbitdaCorrectionParameters ebitdaCorrectionParameters;
    
    public RiskParameters(String id, String name, double amortizationRate, Haircuts haircuts, InterestRates interestRates,
            IndustryMaxLoanDurations industryMaxLoanDurations, double dscrThreshold, Thresholds thresholds,
            CollateralRequirement collateralRequirement, EbitdaCorrectionParameters ebitdaCorrectionParameters) {
        this.id = id;
        this.name = name;
        this.amortizationRate = amortizationRate;
        this.haircuts = haircuts;
        this.interestRates = interestRates;
        this.industryMaxLoanDurations = industryMaxLoanDurations;
        this.dscrThreshold = dscrThreshold;
        this.thresholds = thresholds;
        this.collateralRequirement = collateralRequirement;
        this.ebitdaCorrectionParameters = ebitdaCorrectionParameters;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public int maxLoanDuration(Industry industry, double industryAverage, double ownEquityRatio) {
		int maxLoanDurationForIndustry = industryMaxLoanDurations.maxLoanDuration(industry);
		Optional<Integer> maxLoanDurationCap = thresholds.ownEquityRatioThresholds.maxLoanDuration(ownEquityRatio, industryAverage);
		return maxLoanDurationCap.map(cap -> Math.min(cap, maxLoanDurationForIndustry)).orElse(maxLoanDurationForIndustry);
	}
	
	public Optional<Double> minOwnEquityRatio(double industryAverage, double loanDuration) {
	    return thresholds.ownEquityRatioThresholds.minEquityRatio(industryAverage, loanDuration);
	}
    
}
