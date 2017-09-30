package hu.lae.domain.accounting;

import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.util.MathUtil;

public class BalanceSheet {

    public static BalanceSheet createEmpty() {
        return new BalanceSheet(new Assets(0, 0, 0, 0), new Liabilities(0, 0, 0, 0, 0));
    }
    
    public final Assets assets;
    
    public final Liabilities liabilities;
    
    public BalanceSheet(Assets assets, Liabilities liabilities) {
        this.assets = assets;
        this.liabilities = liabilities;
    }
    
    public double calculateJustifiableShortTermLoan(Haircuts haircuts) {
        double assetsJustifiableValue = assets.evaluate(haircuts);
        double liabilitiesValue = liabilities.shortTermLiabilities();
        return assetsJustifiableValue - liabilitiesValue;
    }
    
    public double liquidityRatio1(double shortTermLoan) {
        return (double)(assets.stock + assets.cash + assets.accountsReceivable + assets.other) / (liabilities.accountsPayable + shortTermLoan); 
    }
    
    public double liquidityRatio2() {
        return (double)(assets.stock + assets.cash + assets.accountsReceivable + assets.other) / (liabilities.accountsPayable); 
    }
    
    public double liquidityRatio3() {
        return (double)(assets.stock + assets.accountsReceivable + assets.other) / (liabilities.accountsPayable); 
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
        
        public double evaluate(Haircuts haircuts) {
            double justifiableValue = MathUtil.directProduct(
                    Arrays.asList((double)accountsReceivable, (double)stock, (double)cash, (double)other),
                    Arrays.asList(haircuts.accountsReceivable, haircuts.stock, haircuts.cash, haircuts.other));
            return justifiableValue;
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
    
    public static class Liabilities {
        
        public final long ownEquity;
        public final long evaluationReserve;
        public final long accountsPayable;
        public final long otherLiabilities;
        public final long total;
        
        public Liabilities(long ownEquity, long evaluationReserve, long accountsPayable, long otherLiabilities, long total) {
            this.ownEquity = ownEquity;
            this.evaluationReserve = evaluationReserve;
            this.accountsPayable = accountsPayable;
            this.otherLiabilities = otherLiabilities;
            this.total = total;
        }

        public double shortTermLiabilities() {
            double value = accountsPayable + otherLiabilities;
            return value;
        }
        
        public double equityRatio() {
            if(total == 0) return 0;  
            return ((double)ownEquity - evaluationReserve) / total;
        }
        
        public double equityRatio(double newLoan) {
            if(total == 0) return 0;  
            return ((double)ownEquity - evaluationReserve) / (total + newLoan);
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
    
}

