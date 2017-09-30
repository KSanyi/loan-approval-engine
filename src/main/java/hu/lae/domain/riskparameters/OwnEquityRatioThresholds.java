package hu.lae.domain.riskparameters;

import java.util.Optional;

public class OwnEquityRatioThresholds {

	public final double threshold1;
	
	public final double threshold2;

	public OwnEquityRatioThresholds(double threshold1, double threshold2) {
		this.threshold1 = threshold1;
		this.threshold2 = threshold2;
	}
	
	public Optional<Integer> maxLoanDuration(double ownEquity, double industryAverage) {
	
		double ratio = ownEquity / industryAverage;
		if(ratio < threshold2) {
			return Optional.of(1);
		} else if(ratio < threshold1) {
			return Optional.of(3);
		} else {
			return Optional.empty();
		}
	}
	
}
