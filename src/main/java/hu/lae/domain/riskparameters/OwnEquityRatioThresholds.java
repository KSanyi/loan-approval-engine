package hu.lae.domain.riskparameters;

import java.util.Optional;

public class OwnEquityRatioThresholds {

	public final double threshold1;
	
	public final int yearForBelowThreshold1;
	
	public final double threshold2;
	
	public final int yearForBelowThreshold2;

	public OwnEquityRatioThresholds(double threshold1, int yearForBelowThreshold1, double threshold2, int yearForBelowThreshold2) {
		this.threshold1 = threshold1;
		this.yearForBelowThreshold1 = yearForBelowThreshold1;
		this.threshold2 = threshold2;
		this.yearForBelowThreshold2 = yearForBelowThreshold2;
	}
	
	public Optional<Integer> maxLoanDuration(double ownEquity, double industryAverage) {
	
		double ratio = ownEquity / industryAverage;
		if(ratio < threshold2) {
			return Optional.of(yearForBelowThreshold2);
		} else if(ratio < threshold1) {
			return Optional.of(yearForBelowThreshold1);
		} else {
			return Optional.empty();
		}
	}
	
}
