package hu.lae.domain;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.accounting.BalanceSheet;
import hu.lae.domain.accounting.IncomeStatement;
import hu.lae.domain.accounting.IncomeStatementData;
import hu.lae.domain.accounting.BalanceSheet.Assets;
import hu.lae.domain.accounting.BalanceSheet.Liabilities;
import hu.lae.domain.loan.ExistingLoans;
import hu.lae.domain.riskparameters.Industry;

public class ClientTest {

    private BalanceSheet balanceSheet = new BalanceSheet(2016,
            new Assets(400, 50, 20, 30),
            new Liabilities(1000, 100, 70, 0, 2000));
    
    private IncomeStatementData incomeStatementData = new IncomeStatementData(Arrays.asList(
            new IncomeStatement(2014, 600, 300, 70, 30, 300),
            new IncomeStatement(2015, 600, 300, 70, 30, 300),
            new IncomeStatement(2016, 600, 300, 70, 30, 300)));
    
    private Client client = new Client("Test client", Industry.AUTOMOTIVE, balanceSheet, incomeStatementData, ExistingLoans.createEmpty());
    
    
    @Test
    public void supplierDays() {
        Assert.assertEquals(85.17, client.supplierDays(), 0.1); 
    }
    
}
