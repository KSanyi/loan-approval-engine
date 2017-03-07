package hu.lae.loan;

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
        
        Haircuts haircuts = new Haircuts(0.8, 0.5, 0.8, 0.4);

        RiskParameters riskParameters = new RiskParameters("id1", "default", 0.4, haircuts,
                new InterestRate(0.03),
                new InterestRate(0.05),
                1.2);
        
        LoanCalculator loanCalculator = new LoanCalculator(riskParameters);

        BalanceSheet balanceSheet = new BalanceSheet(
                new Assets(400, 50, 20, 30),
                new Liabilities(70, 0));
        
        IncomeStatement incomeStatement = new IncomeStatement(2016, 300, 70, 30);
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, 5, 303);
        
        Assert.assertEquals(303, loanApplicationResult.justifiableShortTermLoan, 0.1);
        Assert.assertEquals(1136.7, loanApplicationResult.maxShortTermLoan, 0.1);
        Assert.assertEquals(833.75, loanApplicationResult.maxLongTermLoan, 0.1);
        
        loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, 5, 370);
        
        Assert.assertEquals(766.75, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
}
