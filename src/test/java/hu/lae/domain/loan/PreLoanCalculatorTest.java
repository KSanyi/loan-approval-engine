package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hu.lae.domain.Client;
import hu.lae.domain.finance.BalanceSheet;
import hu.lae.domain.finance.BalanceSheet.Assets;
import hu.lae.domain.finance.BalanceSheet.Liabilities;
import hu.lae.domain.finance.FinancialHistory;
import hu.lae.domain.finance.FinancialStatementData;
import hu.lae.domain.finance.IncomeStatement;
import hu.lae.domain.industry.Industry;
import hu.lae.domain.industry.IndustryData;
import hu.lae.domain.legal.LegalData;
import hu.lae.domain.legal.LegalData.Entity;
import hu.lae.domain.legal.LegalIssueEvaluation;
import hu.lae.domain.legal.LegalIssueEvaluation.EvaluationEntry;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.domain.legal.LegalIssueType.Level;
import hu.lae.domain.legal.LegalParameters;
import hu.lae.domain.riskparameters.CollateralRequirement;
import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.domain.riskparameters.IndustryMaxLoanDurations;
import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.domain.riskparameters.OwnEquityRatioThresholds;
import hu.lae.domain.riskparameters.RiskParameters;
import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.util.MapFactory;
import hu.lae.util.Pair;

public class PreLoanCalculatorTest {

    private LoanPreCalculator loanPreCalculator;
    
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
    
    private Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, ExistingLoans.createEmpty(), 0.015, LegalData.empty);
    
    @Before
    public void init() {
        Haircuts haircuts = new Haircuts(0.8, 0.5, 0.8, 0.4);
        RiskParameters riskParameters = new RiskParameters("id1", "default", 0.4, haircuts, new InterestRates(0.03, 0.05), 
                new IndustryMaxLoanDurations(MapFactory.of(Industry.CONSTRUCTION, 2, Industry.AUTOMOTIVE, 5)),
                1.2, new Thresholds(0.2, 1.2, 0.15, 0.75, 0.8, new OwnEquityRatioThresholds(0.7, 3, 0.5, 1)),
                new CollateralRequirement(MapFactory.of(
                        0.0, new Pair<>(50L, 0.7), 
                        0.02, new Pair<>(30L, 0.4), 
                        0.04, new Pair<>(0L, 0.0))));
        
        Map<Industry, Double> ownEquityRatioAverageMap = MapFactory.of(
        		Industry.AUTOMOTIVE, 0.4,
        		Industry.CONSTRUCTION, 0.5);
		IndustryData industryData = new IndustryData(ownEquityRatioAverageMap);
        
		LegalParameters legalParameters = new LegalParameters(1, 1, Arrays.asList(new LegalIssueEvaluation(LegalIssueType.BANKRUPTCY, new EvaluationEntry(Level.JUDGE, Level.JUDGE, 10), new EvaluationEntry(Level.JUDGE, Level.JUDGE, 10))));
		
		loanPreCalculator = new LoanPreCalculator(riskParameters, legalParameters, industryData);
    }
    
    @Test
    public void maxLoanDuration() {
        int maxLoanDuration = loanPreCalculator.calculateMaxLoanDuration(client, 0);
        
        Assert.assertEquals(5, maxLoanDuration);
    }
    
    @Test
    public void maxLoanDurationWithHistoricalBankrupcy() {
    	
    	LegalData legalData = new LegalData(Arrays.asList(new LegalData.LegalIssue(LegalIssueType.BANKRUPTCY, Optional.of(LocalDate.of(2015, 1, 1)), Entity.COMPANY, Optional.empty())));
    	Client client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, ExistingLoans.createEmpty(), 0.015, legalData);
        
    	int maxLoanDuration = loanPreCalculator.calculateMaxLoanDuration(client, 0);
        
        Assert.assertEquals(1, maxLoanDuration);
    }
    
    @Test
    public void maxLoanDurationWithLowOwnEquity() {
    	
    	FinancialStatementData financialStatementData2016 = new FinancialStatementData(
                2016,
                new BalanceSheet(
                        new Assets(400, 50, 20, 30),
                        new Liabilities(500, 100, 70, 0, 2000)),
                new IncomeStatement(600, 300, 70, 30, 300));
        
        FinancialHistory financialHistory = new FinancialHistory(Arrays.asList(financialStatementData2014, financialStatementData2015, financialStatementData2016));
    	
    	client = new Client("Test client", Industry.AUTOMOTIVE, financialHistory, ExistingLoans.createEmpty(), 0.015, LegalData.empty);
        
    	int maxLoanDuration = loanPreCalculator.calculateMaxLoanDuration(client, 0);
        
        Assert.assertEquals(3, maxLoanDuration);
    }
    
}
