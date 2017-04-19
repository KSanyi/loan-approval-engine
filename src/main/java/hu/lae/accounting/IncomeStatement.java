package hu.lae.accounting;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IncomeStatement {

    public int year;
    
    public final long ebitda;
    
    public final long amortization;
    
    public final long taxes;

    public IncomeStatement(int year, long ebitda, long amortization, long taxes) {
        this.year = year;
        this.ebitda = ebitda;
        this.amortization = amortization;
        this.taxes = taxes;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static IncomeStatement createEmpty(int year) {
        return new IncomeStatement(year, 0, 0, 0);
    }
    
    public static IncomeStatement createDefault(int year) {
        return new IncomeStatement(year, 300, 70, 30);
    }
    
    public int year() {
        return year;
    }
    
}
