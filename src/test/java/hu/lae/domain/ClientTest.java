package hu.lae.domain;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.finance.BalanceSheet;
import hu.lae.domain.finance.FinancialStatementData;
import hu.lae.domain.finance.IncomeStatement;
import hu.lae.domain.finance.BalanceSheet.Assets;
import hu.lae.domain.finance.BalanceSheet.Liabilities;

public class ClientTest {

    FinancialStatementData financialStatementData2016 = new FinancialStatementData(
            2016,
            new BalanceSheet(
                    new Assets(400, 50, 20, 30),
                    new Liabilities(1000, 100, 70, 0, 2000)),
            new IncomeStatement(2000, 300, 70, 30, 300));
    
    @Test
    public void supplierDays() {
        Assert.assertEquals(85.17, financialStatementData2016.supplierDays(), 0.1); 
    }
    
    @Test
    public void buyersDays() {
        Assert.assertEquals(73, financialStatementData2016.buyersDays(), 0.1); 
    }
    
    @Test
    public void stockDays() {
        Assert.assertEquals(9.125, financialStatementData2016.stockDays(), 0.1); 
    }
    
}
