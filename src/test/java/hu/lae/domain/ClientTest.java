package hu.lae.domain;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.accounting.BalanceSheet;
import hu.lae.domain.accounting.BalanceSheet.Assets;
import hu.lae.domain.accounting.BalanceSheet.Liabilities;
import hu.lae.domain.accounting.FinancialStatementData;
import hu.lae.domain.accounting.IncomeStatement;

public class ClientTest {

    FinancialStatementData financialStatementData2016 = new FinancialStatementData(
            2016,
            new BalanceSheet(
                    new Assets(400, 50, 20, 30),
                    new Liabilities(1000, 100, 70, 0, 2000)),
            new IncomeStatement(600, 300, 70, 30, 300));
    
    @Test
    public void supplierDays() {
        Assert.assertEquals(85.17, financialStatementData2016.supplierDays(), 0.1); 
    }
    
}
