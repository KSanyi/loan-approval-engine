package hu.lae.riskparameters;

import java.util.Collections;
import java.util.Map;

public class MaxLoanDurations {

    private final Map<Industry, Integer> maxLoanDurationMap;
    
    public MaxLoanDurations(Map<Industry, Integer> maxLoanDurationMap) {
        this.maxLoanDurationMap = Collections.unmodifiableMap(maxLoanDurationMap);
    }
    
    public int maxLoanDuration(Industry industry) {
        return maxLoanDurationMap.get(industry);
    }
    
}
