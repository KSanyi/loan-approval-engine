package hu.lae.domain.industry;

import java.util.Map;

import hu.lae.util.MapFactory;

public class InMemoryIndustryDataRepository implements IndustryDataRepository {

	IndustryData industryData;
	
	public InMemoryIndustryDataRepository() {
		Map<Industry, Double> ownEquityRatioAverageMap = MapFactory.of(
        		Industry.AUTOMOTIVE, 0.4,
        		Industry.CONSTRUCTION, 0.5);
		industryData = new IndustryData(ownEquityRatioAverageMap);
	}
	
	@Override
	public IndustryData loadIndustryData() {
		return industryData;
	}

	@Override
	public void updateIndustryData(IndustryData industryData) {
		this.industryData = industryData;
	}

}
