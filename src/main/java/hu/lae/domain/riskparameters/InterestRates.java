package hu.lae.domain.riskparameters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class InterestRates {

	public final InterestRate shortTermInterestRate;
    
    public final InterestRate longTermInterestRate;

	public InterestRates(double shortTermInterestRate, double longTermInterestRate) {
		this.shortTermInterestRate = new InterestRate(shortTermInterestRate);
		this.longTermInterestRate = new InterestRate(longTermInterestRate);
	}
	
	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
