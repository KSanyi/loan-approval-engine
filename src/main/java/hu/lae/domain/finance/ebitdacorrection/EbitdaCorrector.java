package hu.lae.domain.finance.ebitdacorrection;

import java.util.stream.DoubleStream;

import hu.lae.domain.finance.YearlyData;
import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;
import hu.lae.util.MathUtil;

public class EbitdaCorrector {

    private final double reasonableEbitdaMarginGrowth;
    
    private final double maxDelta;
    
    private final double minXXX;
    
    private final double maxEbitdaDecrease;
    
    public EbitdaCorrector(EbitdaCorrectionParameters parameters) {
        this.reasonableEbitdaMarginGrowth = parameters.reasonableEbitdaMarginGrowth;
        this.maxDelta = parameters.maxDelta;
        this.minXXX = parameters.minXXX;
        this.maxEbitdaDecrease = parameters.maxEbitdaDecrease;
    }

    public CorrectedEbitdas xxx(YearlyData<EbitdaCorrectionInput> input) {
    	
    	YearlyData<Double> correctedEbitdas = calculateCorrectedEbitdas(input);
    	
    	Double tValue = correctedEbitdas.tMinus1Value * Math.min(correctedEbitdas.tValue / correctedEbitdas.tMinus1Value, 1 + maxDelta);
    	Double tMinus1Value = correctedEbitdas.tMinus2Value * Math.min(correctedEbitdas.tMinus1Value / correctedEbitdas.tMinus2Value, 1 + maxDelta);
    	
    	double twoYearsEbitdaGrowth = correctedEbitdas.tValue / correctedEbitdas.tMinus2Value - 1;
    	double secondYearEbitdaGrowth = correctedEbitdas.tMinus1Value / correctedEbitdas.tMinus2Value - 1;
    	boolean shouldUseTValue = correctedEbitdas.tValue > correctedEbitdas.tMinus1Value && twoYearsEbitdaGrowth < minXXX && secondYearEbitdaGrowth < minXXX;
    	
    	Double tMinus2Value = shouldUseTValue ? tValue : correctedEbitdas.tMinus2Value;
    	
    	double average = DoubleStream.of(tValue, tMinus1Value, tMinus2Value).average().getAsDouble();
    	double averageWithoutTMinus1 = DoubleStream.of(tValue, tMinus2Value).average().getAsDouble();
    	double averageWithoutTMinus2 = DoubleStream.of(tValue, tMinus1Value).average().getAsDouble();
    	
    	boolean decreasingTendency = correctedEbitdas.tValue < correctedEbitdas.tMinus1Value && correctedEbitdas.tValue < correctedEbitdas.tMinus2Value;
    	double decreasingTPlus1 = tValue * (1 + Math.max(correctedEbitdas.tValue / correctedEbitdas.tMinus1Value, maxEbitdaDecrease));
    	double estimatedTPlus1 = decreasingTendency ? decreasingTPlus1 : average;
    	double estimatedTPlus1WihtoutTMinus1 = correctedEbitdas.tValue < correctedEbitdas.tMinus2Value ? decreasingTPlus1 : averageWithoutTMinus1;
    	double estimatedTPlus1WihtoutTMinus2 = correctedEbitdas.tValue < correctedEbitdas.tMinus1Value ? decreasingTPlus1 : averageWithoutTMinus2;
    	
    	return new CorrectedEbitdas(
    	        MathUtil.round(estimatedTPlus1, 2),
    	        MathUtil.round(correctedEbitdas.tValue, 2));
    }
    
    public YearlyData<Double> calculateCorrectedEbitdas(YearlyData<EbitdaCorrectionInput> input) {
        
    	YearlyData<Long> calculateCorrectedSales = calculateCorrectedSales(input);
    	YearlyData<Double> calculateCorrectedEbitdaMargins = calculateCorrectedEbitdaMargins(input);
        
    	return new YearlyData<>(
    			calculateCorrectedSales.tValue * calculateCorrectedEbitdaMargins.tValue,
    			calculateCorrectedSales.tMinus1Value * calculateCorrectedEbitdaMargins.tMinus1Value,
    			calculateCorrectedSales.tMinus2Value * calculateCorrectedEbitdaMargins.tMinus2Value);
    }
    
    private YearlyData<Long> calculateCorrectedSales(YearlyData<EbitdaCorrectionInput> input) {
        
        return new YearlyData<>(
        		calculateCorrectedSales(input.tValue, input.tMinus1Value),
        		calculateCorrectedSales(input.tMinus1Value, input.tMinus2Value),
        		input.tMinus2Value.sales);
    }
    
    private Long calculateCorrectedSales(EbitdaCorrectionInput entry, EbitdaCorrectionInput prevEntry) {
    	 double salesDecrease = entry.sales * (entry.buyersDays - prevEntry.buyersDays) / 365;
         double effectiveSalesDecrease = Double.max(salesDecrease, 0);
         return Math.round(entry.sales - effectiveSalesDecrease);
    }
    
    private YearlyData<Double> calculateCorrectedEbitdaMargins(YearlyData<EbitdaCorrectionInput> input) {
        
    	return new YearlyData<>(
    			calculateCorrectedEbitdaMarginForActualYear(input),
    			calculateCorrectedEbitdaMarginForTMinus1Year(input.tMinus1Value, input.tMinus2Value),
        		input.tMinus2Value.ebitdaMargin());
    }
    
    private Double calculateCorrectedEbitdaMarginForActualYear(YearlyData<EbitdaCorrectionInput> input) {
    	double reasonableEbitdaMargin1 = input.tMinus1Value.ebitdaMargin() * (1 + reasonableEbitdaMarginGrowth);
    	double reasonableEbitdaMargin2 = input.tMinus2Value.ebitdaMargin() * (1 + reasonableEbitdaMarginGrowth) * (1 + reasonableEbitdaMarginGrowth);
    	return MathUtil.min(input.tValue.ebitdaMargin(), reasonableEbitdaMargin1, reasonableEbitdaMargin2);
    }
    
    private Double calculateCorrectedEbitdaMarginForTMinus1Year(EbitdaCorrectionInput entry, EbitdaCorrectionInput prevEntry) {
    	double reasonableEbitdaMargin = prevEntry.ebitdaMargin() * (1 + reasonableEbitdaMarginGrowth);
    	return Math.min(entry.ebitdaMargin(), reasonableEbitdaMargin);
    }
    
}