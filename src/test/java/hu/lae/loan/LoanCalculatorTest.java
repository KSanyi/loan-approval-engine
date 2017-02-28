package hu.lae.loan;

import java.math.BigDecimal;

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
                new BigDecimal("1.0"),
                new BigDecimal("0.8"));

        RiskParameters riskParameters = new RiskParameters("id1", "default", 
                new BigDecimal("0.4"), 
                haircuts,
                new InterestRate(new BigDecimal("0.03")),
                new InterestRate(new BigDecimal("0.03")),
                new BigDecimal("1.2"));
        
        LoanCalculator loanCalculator = new LoanCalculator(riskParameters);

        BalanceSheet balanceSheet = new BalanceSheet(
                new Assets(100, 80, 60, 40),
                new Liabilities(80, 60));
        
        IncomeStatement incomeStatement = new IncomeStatement(2016, 1000, 100, 200);
        
        //loanCalculator.createMaxLoanDistributor(balanceSheet, incomeStatement, 5);
    }
    
}
