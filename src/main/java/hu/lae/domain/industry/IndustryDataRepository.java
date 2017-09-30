package hu.lae.domain.industry;

public interface IndustryDataRepository {

	IndustryData loadIndustryData();

	void updateIndustryData(IndustryData industryData);
	
}
