package hu.lae.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilTest {

    @Test
    public void directProductTest() {
        List<Double> a = Arrays.asList(1.0, 2.0);
        List<Double> b = Arrays.asList(3.0, 4.0);
        Assert.assertEquals(11, MathUtil.directProduct(a, b), 0.01);
    }
    
}
