package hu.lae.riskparameters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Haircuts {

    public final double accountsReceivable;
    
    public final double stock;

    public final double cash;
    
    public final double other;

    public Haircuts(double accountsReceivable, double stock, double cash, double other) {
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
