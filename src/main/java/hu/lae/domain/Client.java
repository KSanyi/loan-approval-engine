package hu.lae.domain;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.finance.BalanceSheet;
import hu.lae.domain.finance.FinancialHistory;
import hu.lae.domain.finance.FinancialStatementData;
import hu.lae.domain.finance.IncomeStatement;
import hu.lae.domain.finance.IncomeStatementHistory;
import hu.lae.domain.finance.BalanceSheet.Assets;
import hu.lae.domain.finance.BalanceSheet.Liabilities;
import hu.lae.domain.industry.Industry;
import hu.lae.domain.legal.LegalData;
import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.domain.legal.LegalData.Entity;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.domain.loan.ExistingLoan;
import hu.lae.domain.loan.ExistingLoans;
import hu.lae.domain.riskparameters.Haircuts;

public class Client {

    public final String name;
    
    public final Industry industry;

    public final FinancialHistory financialHistory;
    
    public final ExistingLoans existingLoans;
    
    public final double pd;
    
    public final LegalData legalData;
    
    public Client(String name, Industry industry, FinancialHistory financialHistory, ExistingLoans existingLoans, double pd, LegalData legalData) {
        this.name = name;
        this.industry = industry;
        this.financialHistory = financialHistory;
        this.existingLoans = existingLoans;
        this.pd = pd;
        this.legalData = legalData;
    }
    
    public double calculateJustifiableShortTermLoan(Haircuts haircuts) {
        return financialStatementData().balanceSheet.calculateJustifiableShortTermLoan(haircuts);
    }

    public FinancialStatementData financialStatementData() {
        return financialHistory.lastFinancialStatementData();
    }
    
    public static Client createDefault() {
        
        FinancialStatementData financialStatementData2016 = new FinancialStatementData(2016, 
                new BalanceSheet(new Assets(400, 50, 20, 30), new Liabilities(1000, 100, 70, 0, 2000)), new IncomeStatement(2000, 200, 70, 30, 300));
        
        FinancialStatementData financialStatementData2015 = new FinancialStatementData(2015, 
                new BalanceSheet(new Assets(300, 45, 15, 25), new Liabilities(900, 90, 65, 0, 1800)), new IncomeStatement(1800, 200, 40, 30, 275));
        
        FinancialStatementData financialStatementData2014 = new FinancialStatementData(2014, 
                new BalanceSheet(new Assets(250, 40, 10, 20), new Liabilities(800, 80, 60, 0, 1600)), new IncomeStatement(1600, 200, 35, 30, 250));
        
        FinancialHistory financialHistory = new FinancialHistory(Arrays.asList(financialStatementData2016, financialStatementData2015, financialStatementData2014));
        return new Client("", Industry.AUTOMOTIVE, financialHistory, 
                new ExistingLoans(Arrays.asList(
                        ExistingLoan.newShortTermLoan(100, false),
                        ExistingLoan.newLongTermLoan(150, LocalDate.of(2021, 2, 12), false),
                        ExistingLoan.newLongTermLoan(200, LocalDate.of(2020, 4, 1), false))),
                0.015,
                new LegalData(Arrays.asList(
                		new LegalIssue(LegalIssueType.BLACK_EMPLOYER, Optional.of(LocalDate.of(2011,1,1)), Entity.COMPANY_GROUP, Optional.empty()),
                		new LegalIssue(LegalIssueType.BANKRUPTCY, Optional.empty(), Entity.COMPANY, Optional.empty()))));
    }

    public IncomeStatementHistory incomeStatementHistory() {
        return financialHistory.incomeStatementHistory();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
