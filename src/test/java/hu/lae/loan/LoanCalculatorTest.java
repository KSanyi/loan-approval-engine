package hu.lae.loan;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.BalanceSheet.Assets;
import hu.lae.accounting.BalanceSheet.Liabilities;
import hu.lae.accounting.IncomeStatement;
import hu.lae.riskparameters.Haircuts;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;

public class LoanCalculatorTest {

    @Test
    public void test() {
        
        Haircuts haircuts = new Haircuts(
                new BigDecimal("0.8"),
                new BigDecimal("0.5"),
                new BigDecimal("0.8"),
                new BigDecimal("0.4"));

        RiskParameters riskParameters = new RiskParameters("id1", "default", 
                new BigDecimal("0.4"), 
                haircuts,
                new InterestRate(new BigDecimal("0.03")),
                new InterestRate(new BigDecimal("0.05")),
                new BigDecimal("1.2"));
        
        LoanCalculator loanCalculator = new LoanCalculator(riskParameters);

        BalanceSheet balanceSheet = new BalanceSheet(
                new Assets(400, 50, 20, 30),
                new Liabilities(70, 0));
        
        IncomeStatement incomeStatement = new IncomeStatement(2016, 300, 70, 30);
        
        MaxLoanDistributor maxLoanDistributor = loanCalculator.createMaxLoanDistributor(balanceSheet, incomeStatement);
        
        Assert.assertEquals(303, maxLoanDistributor.justifiableShortTermloan);
        Assert.assertEquals(1137, maxLoanDistributor.calculateMaxShortTermLoan(5));
        
        Assert.assertEquals(834, maxLoanDistributor.calculateMaxLongTermLoan(5, 303));
        Assert.assertEquals(767, maxLoanDistributor.calculateMaxLongTermLoan(5, 370));
    }
    
}
