package hu.lae.domain.industry;

import java.util.Map;

public class IndustryData {

	private final Map<Industry, Double> ownEquityRatioAverageMap;

	public IndustryData(Map<Industry, Double> ownEquityRatioAverageMap) {
		this.ownEquityRatioAverageMap = ownEquityRatioAverageMap;
	}
	
	public double ownEquityRatioAverage(Industry industry) {
		return ownEquityRatioAverageMap.get(industry);
	}
	
}
