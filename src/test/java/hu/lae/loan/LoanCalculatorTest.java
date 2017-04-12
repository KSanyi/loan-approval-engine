package hu.lae.loan;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.BalanceSheet.Assets;
import hu.lae.accounting.BalanceSheet.Liabilities;
import hu.lae.accounting.IncomeStatement;
import hu.lae.riskparameters.Haircuts;
import hu.lae.riskparameters.InterestRate;
import hu.lae.riskparameters.RiskParameters;

public class LoanCalculatorTest {

    private LoanCalculator loanCalculator;
    
    private BalanceSheet balanceSheet = new BalanceSheet(
            new Assets(400, 50, 20, 30),
            new Liabilities(70, 0));
    
    private IncomeStatement incomeStatement = new IncomeStatement(2016, 300, 70, 30);
    
    private ExistingLoans noExistingLoans = ExistingLoans.createEmpty();
    
    @Before
    public void init() {
        Haircuts haircuts = new Haircuts(0.8, 0.5, 0.8, 0.4);
        RiskParameters riskParameters = new RiskParameters("id1", "default", 0.4, haircuts, new InterestRate(0.03), 5, new InterestRate(0.05), 1.2);
        loanCalculator = new LoanCalculator(riskParameters, LocalDate.of(2017,4,1));
    }
    
    @Test
    public void shortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, noExistingLoans, 5, 0);
        
        Assert.assertEquals(303, loanApplicationResult.justifiableShortTermLoan, 0.1);
        Assert.assertEquals(1136.7, loanApplicationResult.maxShortTermLoan, 0.1);
    }
    
    @Test
    public void tlongTermLoanWithJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, noExistingLoans, 5, 303);
        
        Assert.assertEquals(833.75, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void longTermLoanWithHigherThenJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, noExistingLoans, 5, 370);
        
        Assert.assertEquals(766.75, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void longTermLoanWithLowerThenJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(balanceSheet, incomeStatement, noExistingLoans, 5, 150);
        
        Assert.assertEquals(853.63, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
}
