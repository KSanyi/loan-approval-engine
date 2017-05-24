package hu.lae.domain.accounting;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IncomeStatement {

    public int year;
    
    public final long sales;
    
    public final long ebitda;
    
    public final long amortization;
    
    public final long taxes;
    
    public final long materialExpenditures;

    public IncomeStatement(int year, long sales, long ebitda, long amortization, long taxes, long materialExpenditures) {
        this.year = year;
        this.sales = sales;
        this.ebitda = ebitda;
        this.amortization = amortization;
        this.taxes = taxes;
        this.materialExpenditures = materialExpenditures;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static IncomeStatement createEmpty(int year) {
        return new IncomeStatement(year, 0, 0, 0, 0, 0);
    }
    
    public static IncomeStatement createDefault(int year) {
        return new IncomeStatement(year, 600, 300, 70, 30, 300);
    }
    
    public int year() {
        return year;
    }
    
}