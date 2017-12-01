package hu.lae.domain.finance.ebitdacorrection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CorrectedEbitdas {

    public final double correctedEbitdaAverage;
    
    public final double correctedLastEbitda;
    
    public final double correctedEbitdaAverageWithoutTMinus1;
    
    public final double correctedEbitdaAverageWithoutTMinus2;
    
	public CorrectedEbitdas(double correctedEbitdaAverage, double correctedLastEbitda, double correctedEbitdaAverageWithoutTMinus1, double correctedEbitdaAverageWithoutTMinus2) {
		this.correctedEbitdaAverage = correctedEbitdaAverage;
		this.correctedLastEbitda = correctedLastEbitda;
		this.correctedEbitdaAverageWithoutTMinus1 = correctedEbitdaAverageWithoutTMinus1;
		this.correctedEbitdaAverageWithoutTMinus2 = correctedEbitdaAverageWithoutTMinus2;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
