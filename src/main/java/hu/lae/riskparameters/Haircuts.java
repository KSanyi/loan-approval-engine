package hu.lae.riskparameters;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Haircuts {

    public final BigDecimal accountsReceivable;
    
    public final BigDecimal stock;

    public final BigDecimal cash;
    
    public final BigDecimal other;

    public Haircuts(BigDecimal accountsReceivable, BigDecimal stock, BigDecimal cash, BigDecimal other) {
        this.accountsReceivable = accountsReceivable;
        this.stock = stock;
        this.cash = cash;
        this.other = other;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
