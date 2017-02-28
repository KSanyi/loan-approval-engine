package hu.lae.accounting;

import java.math.BigDecimal;

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
    
    public BigDecimal normalizedFreeCashFlow(BigDecimal amortizationRate) {
        int maintanenceCapex = amortizationRate.multiply(BigDecimal.valueOf(amortization)).intValue();
        return BigDecimal.valueOf(ebitda - maintanenceCapex - taxes);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static IncomeStatement createEmpty(int year) {
        return new IncomeStatement(year, 0, 0, 0);
    }
    
}
