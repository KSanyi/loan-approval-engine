package hu.lae.util;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MathUtil {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public static BigDecimal directProduct(List<Long> a, List<BigDecimal> b) {
        if(a.size() != b.size()) throw new IllegalArgumentException("Size of lists must be equal");
        
        List<String> logElements = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for(int i=0;i<a.size();i++) {
            BigDecimal aValue = BigDecimal.valueOf(a.get(i));
            BigDecimal bValue = b.get(i);
            logElements.add(aValue + " * " + bValue);
            sum = sum.add(aValue.multiply(bValue));
        }
        logger.debug("Calculation: " + String.join(" + ", logElements));
        return sum;
    }
    
    public static BigDecimal sum(long ... elements) {
        logger.debug("Calculation: " + LongStream.of(elements).mapToObj(Long::toString).collect(Collectors.joining(" + ")));
        return BigDecimal.valueOf(LongStream.of(elements).sum());
    }
    
    public static BigDecimal div(BigDecimal a, BigDecimal b) {
        return a.divide(b, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal div(long a, BigDecimal b) {
        return new BigDecimal(a).setScale(2).divide(b, RoundingMode.HALF_UP);
    }
    
}
