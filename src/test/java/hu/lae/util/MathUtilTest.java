package hu.lae.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilTest {

    @Test
    public void directProductTest() {
        List<Long> a = Arrays.asList(1L, 2L);
        List<BigDecimal> b = Arrays.asList(new BigDecimal("3"), new BigDecimal("4"));
        Assert.assertEquals(new BigDecimal("11"), MathUtil.directProduct(a, b));
    }
    
}
