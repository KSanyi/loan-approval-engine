package hu.lae.domain.finance.ebitdacorrection;

import java.util.stream.DoubleStream;

import hu.lae.domain.finance.YearlyData;
import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;
import hu.lae.util.CalculationLog;
import hu.lae.util.MathUtil;

public class EbitdaCorrector {

    private final double reasonableEbitdaMarginGrowth;
    
    private final double maxDelta;
    
    private final double minXXX;
    
    private final double maxEbitdaDecrease;
    
    private final CalculationLog calculationLog = new CalculationLog();
    
    public EbitdaCorrector(EbitdaCorrectionParameters parameters) {
        this.reasonableEbitdaMarginGrowth = parameters.reasonableEbitdaMarginGrowth;
        this.maxDelta = parameters.maxDelta;
        this.minXXX = parameters.minXXX;
        this.maxEbitdaDecrease = parameters.maxEbitdaDecrease;
    }

    public CorrectedEbitdas xxx(YearlyData<EbitdaCorrectionInput> input) {
    	
    	YearlyData<Double> correctedEbitdas = calculateCorrectedEbitdas(input);
    	
    	calculationLog.put("Corrected EBITDA", correctedEbitdas.tValue, correctedEbitdas.tMinus1Value, correctedEbitdas.tMinus2Value);
    	
    	double lastEbitdaGrowth = correctedEbitdas.tValue / correctedEbitdas.tMinus1Value - 1;
    	double secondYearEbitdaGrowth = correctedEbitdas.tMinus1Value / correctedEbitdas.tMinus2Value - 1;
    	double twoYearsEbitdaGrowth = correctedEbitdas.tValue / correctedEbitdas.tMinus2Value - 1;
    	
    	double tValue = lastEbitdaGrowth <= maxDelta ? correctedEbitdas.tValue : correctedEbitdas.tMinus1Value * (1 + maxDelta);
    	double tMinus1Value = secondYearEbitdaGrowth <= maxDelta ? correctedEbitdas.tMinus1Value : correctedEbitdas.tMinus2Value * (1 + maxDelta);
    	
    	/*
    	 *  Ha a T-2es év kiugróan magas (jelentősan nagyobb mint T és mint T-1 ás T nagyobb mint T) de az utolsó évben emelkedett
    	 *  (azaz nem tartósan csökkenő tendenciáról van szó) akkor a T-2es érték helyett az átlagban a T értékkel számol							
    	 */
    	boolean shouldUseTValue = correctedEbitdas.tValue > correctedEbitdas.tMinus1Value && twoYearsEbitdaGrowth < minXXX && secondYearEbitdaGrowth < minXXX;
    	double tMinus2Value = shouldUseTValue ? tValue : correctedEbitdas.tMinus2Value;
    	
    	calculationLog.put("Corrected EBITDA'", tValue, tMinus1Value, tMinus2Value);
    	
    	double average = DoubleStream.of(tValue, tMinus1Value, tMinus2Value).average().getAsDouble();
    	double averageWithoutTMinus1 = calculateAverageWithoutTMinus1(correctedEbitdas, tMinus2Value);
    	double averageWithoutTMinus2 = DoubleStream.of(tValue, correctedEbitdas.tMinus1Value).average().getAsDouble();
    	
    	boolean decreasingTendency = correctedEbitdas.tValue < correctedEbitdas.tMinus1Value && correctedEbitdas.tValue < correctedEbitdas.tMinus2Value;
    	double decreasingTPlus1 = tValue * (1 + Math.max(correctedEbitdas.tValue / correctedEbitdas.tMinus1Value, maxEbitdaDecrease));
    	double estimatedTPlus1 = decreasingTendency ? decreasingTPlus1 : average;
    	double estimatedTPlus1WihtoutTMinus1 = correctedEbitdas.tValue < correctedEbitdas.tMinus2Value ? decreasingTPlus1 : averageWithoutTMinus1;
    	double estimatedTPlus1WihtoutTMinus2 = correctedEbitdas.tValue < correctedEbitdas.tMinus1Value ? decreasingTPlus1 : averageWithoutTMinus2;
    	
    	calculationLog.put("Average", average);
    	calculationLog.put("Average without T-1", averageWithoutTMinus1);
    	calculationLog.put("Average without T-2", averageWithoutTMinus2);
    	
    	calculationLog.put("Estimated T+1 (corrected avg)", MathUtil.round(estimatedTPlus1, 2));
    	calculationLog.put("Estimated T+1 (corrected last)", MathUtil.round(correctedEbitdas.tValue, 2));
    	calculationLog.put("Estimated T+1 without T-1", MathUtil.round(estimatedTPlus1WihtoutTMinus1, 2));
    	calculationLog.put("Estimated T+1 without T-2", MathUtil.round(estimatedTPlus1WihtoutTMinus2, 2));
    	
    	return new CorrectedEbitdas(
    	        MathUtil.round(estimatedTPlus1, 2),
    	        MathUtil.round(correctedEbitdas.tValue, 2),
    	        MathUtil.round(estimatedTPlus1WihtoutTMinus1, 2),
    	        MathUtil.round(estimatedTPlus1WihtoutTMinus2, 2),
    	        calculationLog);
    }
    
    private double calculateAverageWithoutTMinus1(YearlyData<Double> correctedEbitdas, double tMinus2Value) {
    	
    	double twoYearsEbitdaGrowth = correctedEbitdas.tValue / correctedEbitdas.tMinus2Value - 1;
    	double tValue = twoYearsEbitdaGrowth <= maxDelta ? correctedEbitdas.tValue : correctedEbitdas.tMinus2Value * Math.pow(1 + maxDelta, 2);
    	
    	return DoubleStream.of(tValue, tMinus2Value).average().getAsDouble();
    }
    
    public YearlyData<Double> calculateCorrectedEbitdas(YearlyData<EbitdaCorrectionInput> input) {
        
    	YearlyData<Long> calculateCorrectedSales = calculateCorrectedSales(input);
    	YearlyData<Double> calculateCorrectedEbitdaMargins = calculateCorrectedEbitdaMargins(input);
        
    	calculationLog.put("Corrected sales", calculateCorrectedSales.tValue, calculateCorrectedSales.tMinus1Value, calculateCorrectedSales.tMinus2Value);
    	
    	calculationLog.put("Corrected EBITDA margin T", calculateCorrectedEbitdaMargins.tValue, calculateCorrectedEbitdaMargins.tMinus1Value, calculateCorrectedEbitdaMargins.tMinus2Value);
    	
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
