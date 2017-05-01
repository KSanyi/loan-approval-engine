package hu.lae.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Formatters {

    private static final DecimalFormat AMOUNT_FORMAT;
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator((char) 160);
        AMOUNT_FORMAT = new DecimalFormat("###,###", decimalFormatSymbols);
    }
    
    public static String formateAmount(double amount) {
        return AMOUNT_FORMAT.format(amount);
    }
    
}
