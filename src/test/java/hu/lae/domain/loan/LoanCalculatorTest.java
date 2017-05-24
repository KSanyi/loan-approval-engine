package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hu.lae.domain.Client;
import hu.lae.domain.accounting.BalanceSheet;
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.accounting.IncomeStatement;
import hu.lae.domain.accounting.IncomeStatementData;
import hu.lae.domain.accounting.BalanceSheet.Assets;
import hu.lae.domain.accounting.BalanceSheet.Liabilities;
import hu.lae.domain.loan.ExistingLoans;
import hu.lae.domain.loan.LoanApplicationResult;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.domain.riskparameters.Industry;
import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.domain.riskparameters.MaxLoanDurations;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.util.MapFactory;

public class LoanCalculatorTest {

    private LoanCalculator loanCalculator;
    
    private BalanceSheet balanceSheet = new BalanceSheet(
            new Assets(400, 50, 20, 30),
            new Liabilities(1000, 100, 70, 0, 2000));
    
    private IncomeStatementData incomeStatementData = new IncomeStatementData(Arrays.asList(
            new IncomeStatement(2014, 600, 300, 70, 30, 300),
            new IncomeStatement(2015, 600, 300, 70, 30, 300),
            new IncomeStatement(2016, 600, 300, 70, 30, 300)));
    
    private Client client = new Client("Test client", Industry.AUTOMOTIVE, balanceSheet, incomeStatementData, ExistingLoans.createEmpty());
    
    @Before
    public void init() {
        Haircuts haircuts = new Haircuts(0.8, 0.5, 0.8, 0.4);
        RiskParameters riskParameters = new RiskParameters("id1", "default", 0.4, haircuts, new InterestRate(0.03), 
                new MaxLoanDurations(MapFactory.of(Industry.CONSTRUCTION, 2, Industry.AUTOMOTIVE, 5)),
                new InterestRate(0.05), 1.2, new Thresholds(0.2, 1.2));
        loanCalculator = new LoanCalculator(riskParameters, LocalDate.of(2017,4,1));
    }
    
    @Test
    public void maxShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 0, FreeCashFlowCalculator.lastYear, false);
        
        Assert.assertEquals(303, loanApplicationResult.justifiableShortTermLoan, 0.1);
        Assert.assertEquals(1136.7, loanApplicationResult.maxShortTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 303, FreeCashFlowCalculator.lastYear, false);
        
        Assert.assertEquals(833.75, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithFewerPaybackYears() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 3, 303, FreeCashFlowCalculator.lastYear, false);
        
        Assert.assertEquals(524.43, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithHigherThenJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 370, FreeCashFlowCalculator.lastYear, false);
        
        Assert.assertEquals(766.75, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithHigherThenJustifiableShortTermLoanAndFewerPaybackYears() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 3, 370, FreeCashFlowCalculator.lastYear, false);
        
        Assert.assertEquals(482.29, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithLowerThenJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 150, FreeCashFlowCalculator.lastYear, false);
        
        Assert.assertEquals(853.63, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithExistingLoans() {
        ExistingLoans existingLoans = new ExistingLoans(0, 100, LocalDate.of(2019, 1, 1), 0);
        Client client = new Client("Test client", Industry.AUTOMOTIVE, balanceSheet, incomeStatementData, existingLoans);
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 303, FreeCashFlowCalculator.lastYear, false);
        
        Assert.assertEquals(538.39, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithExistingRefinansableLoans() {
        ExistingLoans existingLoans = new ExistingLoans(0, 100, LocalDate.of(2019, 1, 1), 0);
        Client client = new Client("Test client", Industry.AUTOMOTIVE, balanceSheet, incomeStatementData, existingLoans);
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 303, FreeCashFlowCalculator.lastYear, true);
        
        Assert.assertEquals(833.75, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void idealStructure() {
        ExistingLoans existingLoans = new ExistingLoans(10, 100, LocalDate.of(2019, 1, 1), 0);
        Client client = new Client("Test client", Industry.AUTOMOTIVE, balanceSheet, incomeStatementData, existingLoans);
        
        LoanRequest loanRequest = loanCalculator.calculateIdealLoanRequest(client, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(303, loanRequest.shortTermLoan, 0.1);
        Assert.assertEquals(833.75, loanRequest.longTermLoan, 0.1);
    }
    
}