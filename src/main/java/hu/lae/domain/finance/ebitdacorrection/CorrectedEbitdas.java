package hu.lae.domain.finance.ebitdacorrection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CorrectedEbitdas {

    public final double correctedEbitdaAverage;
    
    public final double correctedLastEbitda;
    
    public CorrectedEbitdas(double correctedEbitdaAverage, double correctedLastEbitda) {
        this.correctedEbitdaAverage = correctedEbitdaAverage;
        this.correctedLastEbitda = correctedLastEbitda;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
