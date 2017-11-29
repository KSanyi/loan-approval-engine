package hu.lae.domain.riskparameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.util.Pair;

public class CollateralRequirement {

    public final SortedMap<Double, Entry> map;
    
    public CollateralRequirement(Map<Double, Pair<Long, Double>> map) {
        
        if(map.size() != 3) {
            throw new IllegalArgumentException("All 3 PD entries must be provided");
        }
        
        List<Double> limits = new ArrayList<>(map.keySet());
        Collections.sort(limits);
        
        this.map = new TreeMap<>(Map.of(
                limits.get(0), new Entry("Low PD", map.get(limits.get(0)).v1, map.get(limits.get(0)).v2, "no collateral", "0 coll. value POR or private surety or institutional guarantee"),
                limits.get(1), new Entry("Medium PD", map.get(limits.get(1)).v1, map.get(limits.get(1)).v2, "0 coll. value POR or private surety or portfolio guarantee", "50% RE OR institutional guarantee"),
                limits.get(2), new Entry("High PD", map.get(limits.get(2)).v1, map.get(limits.get(2)).v2, "", "80% RE or min 80% institutional guarantee")));
    }

    public String evaluate(double pd, long allLocalLoan, double debtCapacityUsage) {
        return findEntry(pd).evaluate(allLocalLoan, debtCapacityUsage);
    }
    
    private Entry findEntry(double pd) {
        List<Double> thresholdsReversed = new ArrayList<>(map.keySet());
        Collections.reverse(thresholdsReversed);
        for(double threshold : thresholdsReversed) {
            if(pd > threshold) {
                return map.get(threshold);
            }
        }
        throw new RuntimeException("pd: " + pd + " map: " + map);
    }

    public static class Entry {
        
        public final String name;
        
        public final long amountThreshold;
        
        public final double dcThreshold;
        
        public final String smallTicketResult;
        
        public final String largeTicketResult;

        public Entry(String name, long amountThreshold, double dcThreshold, String smallTicketResult, String largeTicketResult) {
            this.name = name;
            this.amountThreshold = amountThreshold;
            this.dcThreshold = dcThreshold;
            this.smallTicketResult = smallTicketResult;
            this.largeTicketResult = largeTicketResult;
        }
        
        public String evaluate(long allLocalLoan, double debtCapacityUsage) {
            
            if(allLocalLoan < amountThreshold && dcThreshold < debtCapacityUsage) {
                return smallTicketResult;
            } else {
                return largeTicketResult;
            }
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
