package hu.lae.accounting;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.riskparameters.Haircuts;
import hu.lae.util.MathUtil;

public class BalanceSheet {

    public static BalanceSheet createEmpty() {
        return new BalanceSheet(new Assets(0, 0, 0, 0), new Liabilities(0, 0));
    }
    
    public final Assets assets;
    
    public final Liabilities liabilities;
    
    public BalanceSheet(Assets assets, Liabilities liabilities) {
        this.assets = assets;
        this.liabilities = liabilities;
    }
    
    public BigDecimal calculateXXX(Haircuts haircuts) {
        return assets.evaluate(haircuts).subtract(liabilities.evaluate());
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static class Assets {
        
        public final long accountsReceivable;
        
        public final long stock;
        
        public final long cash;
        
        public final long other;

        public Assets(long accountsReceivable, long stock, long cash, long other) {
            this.accountsReceivable = accountsReceivable;
            this.stock = stock;
            this.cash = cash;
            this.other = other;
        }
        
        public BigDecimal evaluate(Haircuts haircuts) {
            BigDecimal xxx =  MathUtil.directProduct(
                    Arrays.asList(accountsReceivable, stock, cash, other),
                    Arrays.asList(haircuts.accountsReceivable, haircuts.stock, haircuts.cash, haircuts.other));
            return xxx;
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
        
    }
    
    public static class Liabilities {
        
        public final long accountsPayable;
        
        public final long otherLiabilities;

        public Liabilities(long accountsPayable, long otherLiabilities) {
            this.accountsPayable = accountsPayable;
            this.otherLiabilities = otherLiabilities;
        }
        
        public BigDecimal evaluate() {
            return MathUtil.sum(accountsPayable, otherLiabilities);
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
        
    }
    
}

