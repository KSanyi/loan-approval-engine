package hu.lae.util;

import java.util.List;

import org.apache.poi.ss.formula.functions.FinanceLib;

public final class ExcelFunctions {

	public static double pmt(double rate, double nper, double pv) {

	    return FinanceLib.pmt(rate, nper, pv, 0, false);
	}

    public static double nper(double rate, double payment, double pv) {
        
        return FinanceLib.nper(rate, payment, pv, 0, false);
    }
    
    public static double npv(double rate, List<Double> payments) {
        
        double[] paymentsArray = payments.stream().mapToDouble(p -> p).toArray();
        
        return FinanceLib.npv(rate, paymentsArray);
    }
	
}
