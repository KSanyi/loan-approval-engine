package hu.lae.domain.accounting;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.accounting.BalanceSheet;
import hu.lae.domain.accounting.BalanceSheet.Assets;
import hu.lae.domain.accounting.BalanceSheet.Liabilities;
import hu.lae.domain.riskparameters.Haircuts;

public class BalanceSheetTest {

    private BalanceSheet balanceSheet = new BalanceSheet(2016,
            new Assets(400, 50, 20, 30),
            new Liabilities(1000, 100, 70, 0, 2000));
    
    @Test
    public void justifiableShortTermLoan() {
        
        Haircuts haircuts = new Haircuts(0.8, 0.5, 1.0, 0.8);
        
        double justifiableShortTermLoan = balanceSheet.calculateJustifiableShortTermLoan(haircuts);
        
        Assert.assertEquals(319, justifiableShortTermLoan, 0.01);
    }
    
    @Test
    public void justifiableShortTermLoanWithTamasData() {
        
        BalanceSheet balanceSheet = new BalanceSheet(2016,
                new Assets(100, 80, 60, 40),
                new Liabilities(1000, 100, 80, 60, 2000));
        
        Haircuts haircuts = new Haircuts(0.8, 0.5, 1.0, 0.8);
        
        double justifiableShortTermLoan = balanceSheet.calculateJustifiableShortTermLoan(haircuts);
        
        Assert.assertEquals(72.0, justifiableShortTermLoan, 0.01);
    }
    
    @Test
    public void liquidityRatio1() {
        
        double liquidityRatio = balanceSheet.liquidityRatio1(303);
        
        Assert.assertEquals(1.34, liquidityRatio, 0.01);
    }
    
    @Test
    public void liquidityRatio2() {
        
        double liquidityRatio = balanceSheet.liquidityRatio2();
        
        Assert.assertEquals(7.14, liquidityRatio, 0.01);
    }
    
    @Test
    public void liquidityRatio3() {
        
        double liquidityRatio = balanceSheet.liquidityRatio3();
        
        Assert.assertEquals(6.85, liquidityRatio, 0.01);
    }
    
    
    
}
