package hu.lae.accounting;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.accounting.BalanceSheet.Assets;
import hu.lae.accounting.BalanceSheet.Liabilities;
import hu.lae.riskparameters.Haircuts;

public class BalanceSheetTest {

    @Test
    public void test() {
        
        BalanceSheet balanceSheet = new BalanceSheet(
                new Assets(100, 80, 60, 40),
                new Liabilities(80, 60));
        
        Haircuts haircuts = new Haircuts(
                new BigDecimal("0.8"),
                new BigDecimal("0.5"),
                new BigDecimal("1.0"),
                new BigDecimal("0.8"));
        
        BigDecimal xxx = balanceSheet.calculateXXX(haircuts);
        
        Assert.assertEquals(new BigDecimal("72.0"), xxx);
    }
    
}
