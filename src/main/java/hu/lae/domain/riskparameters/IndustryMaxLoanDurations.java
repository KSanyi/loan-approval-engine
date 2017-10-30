package hu.lae.domain.riskparameters;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.industry.Industry;

public class IndustryMaxLoanDurations {

    private final Map<Industry, Integer> maxLoanDurationMap;
    
    public IndustryMaxLoanDurations(Map<Industry, Integer> maxLoanDurationMap) {
        this.maxLoanDurationMap = Collections.unmodifiableMap(maxLoanDurationMap);
    }
    
    public int maxLoanDuration(Industry industry) {
        return maxLoanDurationMap.get(industry);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
