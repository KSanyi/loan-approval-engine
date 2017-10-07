package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hu.lae.domain.Client;
import hu.lae.domain.finance.BalanceSheet;
import hu.lae.domain.finance.BalanceSheet.Assets;
import hu.lae.domain.finance.BalanceSheet.Liabilities;
import hu.lae.domain.finance.FinancialHistory;
import hu.lae.domain.finance.FinancialStatementData;
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.finance.IncomeStatement;
import hu.lae.domain.industry.Industry;
import hu.lae.domain.industry.IndustryData;
import hu.lae.domain.riskparameters.CollateralRequirement;
import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.domain.riskparameters.MaxLoanDurations;
import hu.lae.domain.riskparameters.OwnEquityRatioThresholds;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.util.MapFactory;
import hu.lae.util.Pair;

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
    
    private Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, ExistingLoans.createEmpty(), 0.015);

    private ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(client.existingLoans, false);
    
    @Before
    public void init() {
        Haircuts haircuts = new Haircuts(0.8, 0.5, 0.8, 0.4);
        RiskParameters riskParameters = new RiskParameters("id1", "default", 0.4, haircuts, new InterestRates(0.03, 0.05), 
                new MaxLoanDurations(MapFactory.of(Industry.CONSTRUCTION, 2, Industry.AUTOMOTIVE, 5)),
                1.2, new Thresholds(0.2, 1.2, 0.15, 0.75, 0.8, new OwnEquityRatioThresholds(0.7, 3, 0.5, 1)),
                new CollateralRequirement(MapFactory.of(
                        0.0, new Pair<>(50L, 0.7), 
                        0.02, new Pair<>(30L, 0.4), 
                        0.04, new Pair<>(0L, 0.0))));
        
        Map<Industry, Double> ownEquityRatioAverageMap = MapFactory.of(
        		Industry.AUTOMOTIVE, 0.4,
        		Industry.CONSTRUCTION, 0.5);
		IndustryData industryData = new IndustryData(ownEquityRatioAverageMap);
        
        loanCalculator = new LoanCalculator(riskParameters, industryData, LocalDate.of(2017,4,1));
    }
    
    @Test
    public void maxShortTermLoan() {
        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, 0, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        double maxLongTermLoanForMaxShortTermLoan = loanCalculator.calculateMaxLongTermLoan(client, maxShortTermLoan, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(0, maxLongTermLoanForMaxShortTermLoan, 0.1);
        
        double maxLongTermLoanForLessThanMaxShortTermLoan = loanCalculator.calculateMaxLongTermLoan(client, maxShortTermLoan - 100, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        Assert.assertTrue(maxLongTermLoanForLessThanMaxShortTermLoan > 0);
    }
    
    @Test
    public void maxShortTermLoanWithFewerPaybackYears() {
        
        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, 0, 3, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(1389.3, maxShortTermLoan, 0.1);
        
        double maxLongTermLoanForMaxShortTermLoan = loanCalculator.calculateMaxLongTermLoan(client, maxShortTermLoan, 3, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(0, maxLongTermLoanForMaxShortTermLoan, 0.1);
        
        double maxLongTermLoanForLessThanMaxShortTermLoan = loanCalculator.calculateMaxLongTermLoan(client, maxShortTermLoan - 100, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        Assert.assertTrue(maxLongTermLoanForLessThanMaxShortTermLoan > 0);
    }
    
    @Test
    public void maxLongTermLoanWithJustifiableShortTermLoan() {
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 303, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(1086.30, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithFewerPaybackYears() {
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 303, 3, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(683.29, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithHigherThenJustifiableShortTermLoan() {
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 370, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(1019.30, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithHigherThenJustifiableShortTermLoanAndFewerPaybackYears() {
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 370, 3, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(641.14, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithLowerThenJustifiableShortTermLoan() {
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 150, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(1106.18, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithExistingLoans() {
        ExistingLoans existingLoans = new ExistingLoans(Arrays.asList(ExistingLoan.newLongTermLoan(100L, LocalDate.of(2019, 1, 1), false)));
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans, 0.015);
        
        ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(Collections.singletonMap(existingLoans.existingLoans.get(0), false));
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 303, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(790.94, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxLongTermLoanWithExistingRefinansableLoans() {
        ExistingLoans existingLoans = new ExistingLoans(Arrays.asList(ExistingLoan.newLongTermLoan(100L, LocalDate.of(2019, 1, 1), false)));
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans, 0.015);
        ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(client.existingLoans, true);
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 303, 5, existingLoansRefinancing, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(1086.30, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void idealStructure() {
        ExistingLoans existingLoans = new ExistingLoans(Arrays.asList(ExistingLoan.newLongTermLoan(100L, LocalDate.of(2019, 1, 1), false)));
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans, 0.015);
        
        LoanRequest loanRequest = loanCalculator.calculateIdealLoanRequest(client, FreeCashFlowCalculator.lastYear);
        
        Assert.assertEquals(303, loanRequest.shortTermLoan, 0.1);
        Assert.assertEquals(1086.30, loanRequest.longTermLoan, 0.1);
    }
    
    @Test
    public void idealStructureWithAverageCFCalc() {
        ExistingLoans existingLoans = new ExistingLoans(Arrays.asList(ExistingLoan.newLongTermLoan(100L, LocalDate.of(2019, 1, 1), false)));
        Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, existingLoans, 0.015);
        
        LoanRequest loanRequest = loanCalculator.calculateIdealLoanRequest(client, FreeCashFlowCalculator.average);
        
        Assert.assertEquals(303, loanRequest.shortTermLoan, 0.1);
        Assert.assertEquals(785.65, loanRequest.longTermLoan, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithJustifiableShortTermloan() {
        LoanRequest loanRequest = new LoanRequest(200, 400, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, existingLoansRefinancing);
        
        Assert.assertEquals(1.69, minPaybackYears, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithLowerThanJustifiableShortTermloan() {
        LoanRequest loanRequest = new LoanRequest(111, 400, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, existingLoansRefinancing);
        
        Assert.assertEquals(1.66, minPaybackYears, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithHigherThanJustifiableShortTermloan() {
        LoanRequest loanRequest = new LoanRequest(400, 400, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, existingLoansRefinancing);
        
        Assert.assertEquals(1.87, minPaybackYears, 0.1);
    }
    
    @Test
    public void minPaybackYearsWithExtraHighLongTermloan() {
        LoanRequest loanRequest = new LoanRequest(400, 1000, 5);
        
        double minPaybackYears = loanCalculator.calculateMinPaybackYears(client, loanRequest, FreeCashFlowCalculator.lastYear, existingLoansRefinancing);
        
        Assert.assertEquals(5.06, minPaybackYears, 0.1);
    }
    
}
