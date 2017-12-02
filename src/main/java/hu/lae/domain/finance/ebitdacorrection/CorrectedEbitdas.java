package hu.lae.domain.finance.ebitdacorrection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.util.CalculationLog;

public class CorrectedEbitdas {

    public final double correctedEbitdaAverage;
    
    public final double correctedLastEbitda;
    
    public final double correctedEbitdaAverageWithoutTMinus1;
    
    public final double correctedEbitdaAverageWithoutTMinus2;
    
    public final CalculationLog calculationLog;
    
	public CorrectedEbitdas(double correctedEbitdaAverage, double correctedLastEbitda, double correctedEbitdaAverageWithoutTMinus1, double correctedEbitdaAverageWithoutTMinus2, CalculationLog calculationLog) {
		this.correctedEbitdaAverage = correctedEbitdaAverage;
		this.correctedLastEbitda = correctedLastEbitda;
		this.correctedEbitdaAverageWithoutTMinus1 = correctedEbitdaAverageWithoutTMinus1;
		this.correctedEbitdaAverageWithoutTMinus2 = correctedEbitdaAverageWithoutTMinus2;
		this.calculationLog = calculationLog;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
