package hu.lae.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Formatters {

    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private static final DecimalFormat AMOUNT_FORMAT;
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator((char) 160);
        AMOUNT_FORMAT = new DecimalFormat("###,###", decimalFormatSymbols);
    }
    
    public static String formateAmount(double amount) {
        return AMOUNT_FORMAT.format(amount);
    }
    
    public static String formatYears(double years) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(DF.format(years));
        sb.append(" (");
        
        int months = (int)Math.ceil(years * 12);
        if(months % 12 == 0) {
            sb.append(formatUnit(months / 12, "year"));
        } else if(months < 12) {
            sb.append(formatUnit(months, "month"));
        } else {
            sb.append(formatUnit(months / 12, "year" + " " + formatUnit(months % 12, "month")));
        }
        sb.append(")");
        
        return sb.toString();
    }

    private static String formatUnit(int number, String unit) {
        if(number == 1) {
            return 1 + " " + unit;
        } else {
            return number + " " + unit + "s";
        }
    }
    
}
