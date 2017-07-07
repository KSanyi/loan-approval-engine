package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hu.lae.domain.Client;
import hu.lae.domain.accounting.BalanceSheet;
import hu.lae.domain.accounting.BalanceSheet.Assets;
import hu.lae.domain.accounting.BalanceSheet.Liabilities;
import hu.lae.domain.accounting.FinancialHistory;
import hu.lae.domain.accounting.FinancialStatementData;
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.accounting.IncomeStatement;
import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.domain.riskparameters.Industry;
import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.domain.riskparameters.MaxLoanDurations;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.util.MapFactory;

public class LoanCalculatorTest {

    private LoanCalculator loanCalculator;
    
    FinancialStatementData financialStatementData2016 = new FinancialStatementData(
            2016,
            new BalanceSheet(
                    new Assets(400, 50, 20, 30),
                    new Liabilities(1000, 100, 70, 0, 2000)),
            new IncomeStatement(600, 300, 70, 30, 300));
    
    FinancialStatementData financialStatementData2015 = new FinancialStatementData(
            2015,
            new BalanceSheet(
                    new Assets(400, 50, 20, 30),
                    new Liabilities(1000, 100, 70, 0, 2000)),
            new IncomeStatement(500, 200, 70, 30, 300));
    
    FinancialStatementData financialStatementData2014 = new FinancialStatementData(
            2014,
            new BalanceSheet(
                    new Assets(400, 50, 20, 30),
                    new Liabilities(1000, 100, 70, 0, 2000)),
            new IncomeStatement(400, 150, 70, 30, 300));
    
    FinancialHistory financialHistory = new FinancialHistory(Arrays.asList(financialStatementData2014, financialStatementData2015, financialStatementData2016));
    
    private Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, ExistingLoans.createEmpty());

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
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 0, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(303, loanApplicationResult.justifiableShortTermLoan, 0.1);
        Assert.assertEquals(1389.3, loanApplicationResult.maxShortTermLoan, 0.1);
        
        LoanApplicationResult resultForMaxShortTermLoan = loanCalculator.calculate(client, 5, loanApplicationResult.maxShortTermLoan, FreeCashFlowCalculator.lastYear, false, false);
        Assert.assertEquals(0, resultForMaxShortTermLoan.maxLongTermLoan, 0.1);
        
        LoanApplicationResult resultForLessThenMaxShortTermLoan = loanCalculator.calculate(client, 3, loanApplicationResult.maxShortTermLoan - 100, FreeCashFlowCalculator.lastYear, false, false);
        Assert.assertTrue(resultForLessThenMaxShortTermLoan.maxLongTermLoan > 0);
    }
    
    @Test
    public void maxShortTermLoanWithFewerPaybackYears() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 3, 0, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(303, loanApplicationResult.justifiableShortTermLoan, 0.1);
        Assert.assertEquals(1389.3, loanApplicationResult.maxShortTermLoan, 0.1);
        
        LoanApplicationResult resultForMaxShortTermLoan = loanCalculator.calculate(client, 3, loanApplicationResult.maxShortTermLoan, FreeCashFlowCalculator.lastYear, false, false);
        Assert.assertEquals(0, resultForMaxShortTermLoan.maxLongTermLoan, 0.1);
        
        LoanApplicationResult resultForLessThenMaxShortTermLoan = loanCalculator.calculate(client, 3, loanApplicationResult.maxShortTermLoan - 100, FreeCashFlowCalculator.lastYear, false, false);
        Assert.assertTrue(resultForLessThenMaxShortTermLoan.maxLongTermLoan > 0);
    }
    
    @Test
    public void maxLongTermLoanWithJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 303, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(1086.30, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithFewerPaybackYears() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 3, 303, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(683.29, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithHigherThenJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 370, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(1019.30, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithHigherThenJustifiableShortTermLoanAndFewerPaybackYears() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 3, 370, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(641.14, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithLowerThenJustifiableShortTermLoan() {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 150, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(1106.18, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithExistingLoans() {
        ExistingLoans existingLoans = new ExistingLoans(0, 100, LocalDate.of(2019, 1, 1), 0);
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans);
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 303, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(790.94, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithExistingRefinansableLoans() {
        ExistingLoans existingLoans = new ExistingLoans(0, 100, LocalDate.of(2019, 1, 1), 0);
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans);
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, 5, 303, FreeCashFlowCalculator.lastYear, false, true);
        
        Assert.assertEquals(1086.30, loanApplicationResult.maxLongTermLoan, 0.1);
    }
    
    @Test
    public void idealStructure() {
        ExistingLoans existingLoans = new ExistingLoans(10, 100, LocalDate.of(2019, 1, 1), 0);
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans);
        
        LoanRequest loanRequest = loanCalculator.calculateIdealLoanRequest(client, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(303, loanRequest.shortTermLoan, 0.1);
        Assert.assertEquals(1086.30, loanRequest.longTermLoan, 0.1);
    }
    
    @Test
    public void idealStructureWithAverageCFCalc() {
        ExistingLoans existingLoans = new ExistingLoans(10, 100, LocalDate.of(2019, 1, 1), 0);
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans);
        
        LoanRequest loanRequest = loanCalculator.calculateIdealLoanRequest(client, FreeCashFlowCalculator.average);
        
        Assert.assertEquals(303, loanRequest.shortTermLoan, 0.1);
        Assert.assertEquals(785.65, loanRequest.longTermLoan, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithJustifiableShortTermloan() {
        LoanRequest loanRequest = new LoanRequest(200, 400, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(1.69, minPaybackYears, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithLowerThanJustifiableShortTermloan() {
        LoanRequest loanRequest = new LoanRequest(111, 400, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(1.66, minPaybackYears, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithHigherThanJustifiableShortTermloan() {
        LoanRequest loanRequest = new LoanRequest(400, 400, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(1.87, minPaybackYears, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithExtraHighLongTermloan() {
        LoanRequest loanRequest = new LoanRequest(400, 1000, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, false, false);
        
        Assert.assertEquals(5.06, minPaybackYears, 0.1);
    }
    
}
