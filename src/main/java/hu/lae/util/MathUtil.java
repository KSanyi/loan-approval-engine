package hu.lae.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

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
    
    public static double round(double value, int digits) {
        
        double x = Math.pow(10, digits);
        return Math.round(value * x) / x;
    }
    
    public static double min(double ... values) {
        
        return DoubleStream.of(values).min().orElse(0);
    }
    
    public static double min(List<Double> values) {
        
        return values.stream().mapToDouble(d -> d).min().orElse(0);
    }
    
    public static double max(double ... values) {
        
        return DoubleStream.of(values).max().orElse(0);
    }
    
}
