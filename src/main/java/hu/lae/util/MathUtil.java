package hu.lae.util;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MathUtil {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public static double directProduct(List<Double> a, List<Double> b) {
        if(a.size() != b.size()) throw new IllegalArgumentException("Size of lists must be equal");
        
        List<String> logElements = new ArrayList<>();
        double sum = 0;
        for(int i=0;i<a.size();i++) {
            logElements.add(a.get(i) + " * " + b.get(i));
            sum += a.get(i) * b.get(i);
        }
        logger.debug("Calculation: " + String.join(" + ", logElements));
        return sum;
    }
    
}
