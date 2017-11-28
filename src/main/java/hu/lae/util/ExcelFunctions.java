package hu.lae.util;

import org.apache.poi.ss.formula.functions.FinanceLib;

public final class ExcelFunctions {

	public static double pmt(double rate, double nper, double pv) {

	    return FinanceLib.pmt(rate, nper, pv, 0, false);
	}

    public static double nper(double rate, double payment, double pv) {
        
        return FinanceLib.nper(rate, payment, pv, 0, false);
    }
    
    public static double pv(double rate, double nper, double payment) {
        
        return FinanceLib.pv(rate, nper, payment, 0, false);
    }
	
}
