package hu.lae.accounting;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.accounting.BalanceSheet.Assets;
import hu.lae.accounting.BalanceSheet.Liabilities;
import hu.lae.riskparameters.Haircuts;

public class BalanceSheetTest {

    @Test
    public void justifiableShortTermLoan() {
        
        BalanceSheet balanceSheet = new BalanceSheet(
                new Assets(100, 80, 60, 40),
                new Liabilities(80, 60));
        
        Haircuts haircuts = new Haircuts(0.8, 0.5, 1.0, 0.8);
        
        double justifiableShortTermLoan = balanceSheet.calculateJustifiableShortTermLoan(haircuts);
        
        Assert.assertEquals(72.0, justifiableShortTermLoan, 0.01);
    }
    
    @Test
    public void liquidityRatio() {
        
        BalanceSheet balanceSheet = new BalanceSheet(
                new Assets(400, 50, 20, 30),
                new Liabilities(70, 0));
        
        double liquidityRatio = balanceSheet.liquidityRatio(303);
        
        Assert.assertEquals(1.34, liquidityRatio, 0.01);
    }
    
    
    
}
