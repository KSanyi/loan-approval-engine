package hu.lae.domain.finance.ebitdacorrection;

import hu.lae.domain.finance.YearlyData;
import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;
import hu.lae.util.CalculationLog;
import hu.lae.util.MathUtil;

public class EbitdaCorrector {

    private final double reasonableEbitdaMarginGrowth;
    
    private final double maxDelta;
    
    private final double minXXX;
    
    private final double maxEbitdaDecrease;
    
    private final YearlyData<Double> yearlyWeights;
    
    private final CalculationLog calculationLog = new CalculationLog();
    
    public EbitdaCorrector(EbitdaCorrectionParameters parameters) {
        this.reasonableEbitdaMarginGrowth = parameters.reasonableEbitdaMarginGrowth;
        this.maxDelta = parameters.maxDelta;
        this.minXXX = parameters.minXXX;
        this.maxEbitdaDecrease = parameters.maxEbitdaDecrease;
        this.yearlyWeights = parameters.yearlyWeights;
    }

    public CorrectedEbitdas xxx(YearlyData<EbitdaCorrectionInput> input) {
    	
    	YearlyData<Double> correctedEbitdas = calculateCorrectedEbitdas(input);
    	
    	calculationLog.put("Corrected EBITDA", correctedEbitdas.tValue, correctedEbitdas.tMinus1Value, correctedEbitdas.tMinus2Value);
    	
    	double lastEbitdaGrowth = calculateGrowth(correctedEbitdas.tValue, correctedEbitdas.tMinus1Value);
    	double secondYearEbitdaGrowth = calculateGrowth(correctedEbitdas.tMinus1Value, correctedEbitdas.tMinus2Value);
    	double twoYearsEbitdaGrowth = calculateGrowth(correctedEbitdas.tValue, correctedEbitdas.tMinus2Value);
    	
    	double tValue = lastEbitdaGrowth <= maxDelta ? correctedEbitdas.tValue : correctedEbitdas.tMinus1Value * (1 + maxDelta);
    	double tMinus1Value = secondYearEbitdaGrowth <= maxDelta ? correctedEbitdas.tMinus1Value : correctedEbitdas.tMinus2Value * (1 + maxDelta);
    	
    	/*
    	 *  Ha a T-2es év kiugróan magas (jelentősan nagyobb mint T és mint T-1 ás T nagyobb mint T) de az utolsó évben emelkedett
    	 *  (azaz nem tartósan csökkenő tendenciáról van szó) akkor a T-2es érték helyett az átlagban a T értékkel számol							
    	 */
    	boolean shouldUseTValue = correctedEbitdas.tValue > correctedEbitdas.tMinus1Value && twoYearsEbitdaGrowth < minXXX && secondYearEbitdaGrowth < minXXX;
    	double tMinus2Value = shouldUseTValue ? tValue : correctedEbitdas.tMinus2Value;
    	
    	calculationLog.put("Corrected EBITDA'", tValue, tMinus1Value, tMinus2Value);
    	
    	double average = calculateAverage(tValue, tMinus1Value, tMinus2Value);
    	double averageWithoutTMinus2 = calculateAverage(tValue, correctedEbitdas.tMinus1Value, 0);
    	
    	boolean decreasingTendency = correctedEbitdas.tValue < correctedEbitdas.tMinus1Value && correctedEbitdas.tValue < correctedEbitdas.tMinus2Value;
    	double decreasingTPlus1 = tValue * (1 + Math.max(correctedEbitdas.tValue / correctedEbitdas.tMinus1Value, maxEbitdaDecrease));
    	double estimatedTPlus1 = decreasingTendency ? decreasingTPlus1 : average;
    	
    	double estimatedTPlus1WihtoutTMinus1 = calculateEstimateWithoutTMinus1(input, correctedEbitdas);
    	double estimatedTPlus1WihtoutTMinus2 = correctedEbitdas.tValue < correctedEbitdas.tMinus1Value ? decreasingTPlus1 : averageWithoutTMinus2;
    	
    	calculationLog.put("Average", average);
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
    
    private double calculateGrowth(double value, double previousValue) {
    	
    	double divisor = previousValue != 0 ? Math.abs(previousValue) : 0.00001;
    	
    	return (value - previousValue) / divisor;
    }
    
    private double calculateEstimateWithoutTMinus1(YearlyData<EbitdaCorrectionInput> input, YearlyData<Double> correctedEbitdas) {
    	
    	double ebitdaMargin = input.tValue.ebitdaMargin();
    	double reasonableEbitdaMargin2 = input.tMinus2Value.ebitdaMargin() * (1 + reasonableEbitdaMarginGrowth); // ??? * (1 + reasonableEbitdaMarginGrowth);
    	double usedEbitdaMargin = Math.min(ebitdaMargin, reasonableEbitdaMargin2);
    	
    	YearlyData<Long> correctedSales = calculateCorrectedSales(input);
    	double xxx = correctedSales.tValue * usedEbitdaMargin;
    	
    	double growth = calculateGrowth(xxx, correctedEbitdas.tMinus2Value);
    	
    	double tValue = growth <= maxDelta ? xxx : correctedEbitdas.tMinus2Value * (1 + maxDelta);
    	
    	double average = calculateAverage(tValue, 0, correctedEbitdas.tMinus2Value);
    	
    	return tValue < correctedEbitdas.tMinus2Value ? tValue * (1 + Math.max(growth, minXXX)) : average;
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
    	double reasonableEbitdaMargin2 = input.tMinus2Value.ebitdaMargin() * (1 + reasonableEbitdaMarginGrowth); // ??? * (1 + reasonableEbitdaMarginGrowth);
    	return MathUtil.min(input.tValue.ebitdaMargin(), MathUtil.max(reasonableEbitdaMargin1, reasonableEbitdaMargin2));
    }
    
    private Double calculateCorrectedEbitdaMarginForTMinus1Year(EbitdaCorrectionInput entry, EbitdaCorrectionInput prevEntry) {
    	double reasonableEbitdaMargin = prevEntry.ebitdaMargin() * (1 + reasonableEbitdaMarginGrowth);
    	return Math.min(entry.ebitdaMargin(), reasonableEbitdaMargin);
    }
    
    private double calculateAverage(double tValue, double tMinus1Value, double tMinus2Value) {
        
        return yearlyWeights.tValue * tValue + yearlyWeights.tMinus1Value * tMinus1Value + yearlyWeights.tMinus2Value * tMinus2Value; 
    }
    
}
