package hu.lae.domain.finance.ebitdacorrection;

import java.util.ArrayList;
import java.util.List;

import hu.lae.util.MathUtil;

public class EbitdaCorrector {

    private final double reasonableEbitdaMarginGrowth;
    
    public EbitdaCorrector(double reasonableEbitdaMarginGrowth) {
        this.reasonableEbitdaMarginGrowth = reasonableEbitdaMarginGrowth;
    }

    public List<Long> calculateCorrectedEbitdas(List<EbitdaCorretionInput> input) {
        
        List<Long> calculateCorrectedSales = calculateCorrectedSales(input);
        List<Double> calculateCorrectedEbitdaMargins = calculateCorrectedEbitdaMargins(input);
        
        List<Long> correctedEbitdaList = new ArrayList<>();
        for(int i=0;i<input.size();i++) {
            Long correctedEbitda = Math.round(calculateCorrectedSales.get(i) * calculateCorrectedEbitdaMargins.get(i));
            correctedEbitdaList.add(correctedEbitda);
        }
        
        return correctedEbitdaList;
    }
    
    private List<Long> calculateCorrectedSales(List<EbitdaCorretionInput> input) {
        
        List<Long> correctedSalesList = new ArrayList<>();
        for(int i=0;i<input.size()-1;i++) {
            double salesDecrease = input.get(i).sales * (input.get(i).buyersDays - input.get(i+1).buyersDays) / 365;
            double effectiveSalesDecrease = Double.max(salesDecrease, 0);
            long correctedSales = Math.round(input.get(i).sales - effectiveSalesDecrease);
            correctedSalesList.add(correctedSales);       
        }
        correctedSalesList.add(input.get(input.size()-1).sales);
        
        return correctedSalesList;
        
    }
    
    private List<Double> calculateCorrectedEbitdaMargins(List<EbitdaCorretionInput> input) {
        
        List<Double> correctedEbitdaMarginList = new ArrayList<>();
        for(int i=0;i<input.size()-1;i++) {
            List<Double> reasonableEbitdaMargins = new ArrayList<>();
            for(int j=i+1;j<input.size();j++) {
                double reasonableEbitdaMargin = input.get(j).ebitdaMargin() * Math.pow(1 + reasonableEbitdaMarginGrowth, j);
                reasonableEbitdaMargins.add(reasonableEbitdaMargin);
            }
            
            double effectiveEbitdaMargin = MathUtil.min(input.get(i).ebitdaMargin(), MathUtil.min(reasonableEbitdaMargins));
            correctedEbitdaMarginList.add(effectiveEbitdaMargin);       
        }
        correctedEbitdaMarginList.add(input.get(input.size()-1).ebitdaMargin());
        
        return correctedEbitdaMarginList;
    }
    
    
    
}
