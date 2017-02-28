package hu.lae.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.LongStream;

public class MathUtil {

    public static BigDecimal directProduct(List<Long> a, List<BigDecimal> b) {
        if(a.size() != b.size()) throw new IllegalArgumentException("Size of lists must be equal");
        
        BigDecimal sum = BigDecimal.ZERO;
        for(int i=0;i<a.size();i++) {
            sum = sum.add(BigDecimal.valueOf(a.get(i)).multiply(b.get(i)));
        }
        return sum;
    }
    
    public static BigDecimal sum(long ... elements) {
        return BigDecimal.valueOf(LongStream.of(elements).sum());
    }
    
    public static BigDecimal div(BigDecimal a, BigDecimal b) {
        return a.divide(b, RoundingMode.HALF_UP);
    }
    
}
