package hu.lae.util;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {

    
    public static double directProduct(List<Double> a, List<Double> b) {
        if(a.size() != b.size()) throw new IllegalArgumentException("Size of lists must be equal");
        
        List<String> logElements = new ArrayList<>();
        double sum = 0;
        for(int i=0;i<a.size();i++) {
            logElements.add(a.get(i) + " * " + b.get(i));
            sum += a.get(i) * b.get(i);
        }
        return sum;
    }
    
}
