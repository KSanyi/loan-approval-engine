package hu.lae.domain;

import java.time.LocalDate;
import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.accounting.BalanceSheet;
import hu.lae.domain.accounting.BalanceSheet.Assets;
import hu.lae.domain.accounting.BalanceSheet.Liabilities;
import hu.lae.domain.accounting.FinancialHistory;
import hu.lae.domain.accounting.FinancialStatementData;
import hu.lae.domain.accounting.IncomeStatement;
import hu.lae.domain.accounting.IncomeStatementHistory;
import hu.lae.domain.loan.ExistingLoan;
import hu.lae.domain.loan.ExistingLoans;
import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.domain.riskparameters.Industry;

public class Client {

    public final String name;
    
    public final Industry industry;

    public final FinancialHistory financialHistory;
    
    public final ExistingLoans existingLoans;
    
    public Client(String name, Industry industry, FinancialHistory financialHistory, ExistingLoans existingLoans) {
        this.name = name;
        this.industry = industry;
        this.financialHistory = financialHistory;
        this.existingLoans = existingLoans;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public double calculateJustifiableShortTermLoan(Haircuts haircuts) {
        return financialStatementData().balanceSheet.calculateJustifiableShortTermLoan(haircuts);
    }

    public FinancialStatementData financialStatementData() {
        return financialHistory.lastFinancialStatementData();
    }
    
    public static Client createDefault() {
        
        FinancialStatementData financialStatementData2016 = new FinancialStatementData(2016, 
                new BalanceSheet(new Assets(400, 50, 20, 30), new Liabilities(1000, 100, 70, 0, 2000)), new IncomeStatement(2000, 300, 70, 30, 300));
        
        FinancialStatementData financialStatementData2015 = new FinancialStatementData(2015, 
                new BalanceSheet(new Assets(300, 45, 15, 25), new Liabilities(900, 90, 65, 0, 1800)), new IncomeStatement(1800, 250, 40, 30, 275));
        
        FinancialStatementData financialStatementData2014 = new FinancialStatementData(2014, 
                new BalanceSheet(new Assets(250, 40, 10, 20), new Liabilities(800, 80, 60, 0, 1600)), new IncomeStatement(1600, 200, 35, 30, 250));
        
        FinancialHistory financialHistory = new FinancialHistory(Arrays.asList(financialStatementData2016, financialStatementData2015, financialStatementData2014));
        return new Client("", Industry.AUTOMOTIVE, financialHistory, 
                new ExistingLoans(Arrays.asList(
                        ExistingLoan.newLongTermLoan(100, LocalDate.of(2021, 2, 12), false),
                        ExistingLoan.newShortTermLoan(50, true))));
    }

    public IncomeStatementHistory incomeStatementHistory() {
        return financialHistory.incomeStatementHistory();
    }
}
