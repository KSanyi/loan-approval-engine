package hu.lae.accounting;

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
        
        Haircuts haircuts = new Haircuts(0.8, 0.5, 1.0, 0.8);
        
        double xxx = balanceSheet.calculateXXX(haircuts);
        
        Assert.assertEquals(72.0, xxx, 0.01);
    }
    
}
