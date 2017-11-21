package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Arrays;

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
import hu.lae.domain.legal.LegalData;
import hu.lae.domain.riskparameters.CollateralRequirement;
import hu.lae.domain.riskparameters.EbitdaCorrectionParameters;
import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.domain.riskparameters.IndustryMaxLoanDurations;
import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.domain.riskparameters.OwnEquityRatioThresholds;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.util.MapFactory;
import hu.lae.util.Pair;

public class LoanCalculatorTest2 {

    private LoanCalculator loanCalculator;
    
    FinancialStatementData financialStatementData2016 = new FinancialStatementData(
            2016,
            new BalanceSheet(
                    new Assets(400, 50, 20, 30),
                    new Liabilities(1000, 100, 70, 0, 2000)),
            new IncomeStatement(2000, 300, 70, 30, 300));
    
    FinancialStatementData financialStatementData2015 = new FinancialStatementData(
            2015,
            new BalanceSheet(
                    new Assets(300, 45, 15, 25),
                    new Liabilities(900, 90, 65, 0, 1800)),
            new IncomeStatement(1800, 250, 40, 30, 275));
    
    FinancialStatementData financialStatementData2014 = new FinancialStatementData(
            2014,
            new BalanceSheet(
                    new Assets(250, 40, 10, 20),
                    new Liabilities(800, 80, 60, 0, 1600)),
            new IncomeStatement(1600, 200, 35, 30, 250));
    
    FinancialHistory financialHistory = new FinancialHistory(Arrays.asList(financialStatementData2014, financialStatementData2015, financialStatementData2016));
    
    private Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, 
            new ExistingLoans(Arrays.asList(
                    ExistingLoan.newShortTermLoan(150, false),
                    ExistingLoan.newLongTermLoan(150, LocalDate.of(2021,2,12), false),
                    ExistingLoan.newLongTermLoan(200, LocalDate.of(2020,4,1), false))), 
            0.015, LegalData.empty);

    private ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(client.existingLoans, false);
    
    @Before
    public void init() {
        Haircuts haircuts = new Haircuts(0.8, 0.5, 0.8, 0.4);
        RiskParameters riskParameters = new RiskParameters("id1", "default", 0.4, haircuts, new InterestRates(0.10, 0.05),
                new IndustryMaxLoanDurations(MapFactory.of(Industry.CONSTRUCTION, 2, Industry.AUTOMOTIVE, 5)),
                1.2, new Thresholds(0.2, 1.2, 0.15, 0.75, 0.8, new OwnEquityRatioThresholds(0.7, 3, 0.5, 1)),
                new CollateralRequirement(MapFactory.of(
                        0.0, new Pair<>(50L, 0.7), 
                        0.02, new Pair<>(30L, 0.4), 
                        0.04, new Pair<>(0L, 0.0))),
                new EbitdaCorrectionParameters(0.3, 0.1, -0.1, -0.05));
        
        loanCalculator = new LoanCalculator(riskParameters, LocalDate.of(2017,4,1), 5);
    }
    
    @Test
    public void idealLoanRequest() {
        LoanRequest idealLoanRequest = loanCalculator.calculateIdealLoanRequest(client, FreeCashFlowCalculator.average);
        
        Assert.assertEquals(303, idealLoanRequest.shortTermLoan, 0.1);
        Assert.assertEquals(736, idealLoanRequest.longTermLoan, 0.1);
    }
    
    @Test
    public void maxShortTermLoan() {
        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, 100, 5, existingLoansRefinancing, FreeCashFlowCalculator.average);
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 50, 5, existingLoansRefinancing, FreeCashFlowCalculator.average);
        
        Assert.assertEquals(159.61, maxShortTermLoan, 0.1);
        Assert.assertEquals(151.21, maxLongTermLoan, 0.1);
    }
    
    @Test
    public void maxShortTermLoan2() {
        
        Client client = new Client("Test client2", Industry.AUTOMOTIVE, financialHistory, 
                new ExistingLoans(Arrays.asList(
                        ExistingLoan.newShortTermLoan(350, false),
                        ExistingLoan.newLongTermLoan(50, LocalDate.of(2021,2,12), false),
                        ExistingLoan.newLongTermLoan(200, LocalDate.of(2020,4,1), false))), 
                0.015, LegalData.empty);
        ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(client.existingLoans, false);
        
        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, 200, 5, existingLoansRefinancing, FreeCashFlowCalculator.average);
        
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, 50, 5, existingLoansRefinancing, FreeCashFlowCalculator.average);
        
        Assert.assertEquals(21.42, maxShortTermLoan, 0.1);
        Assert.assertEquals(171.42, maxLongTermLoan, 0.1);
    }
   
}
