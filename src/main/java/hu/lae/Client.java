package hu.lae;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.IncomeStatementData;
import hu.lae.loan.ExistingLoans;
import hu.lae.riskparameters.Industry;

public class Client {

    public final String name;
    
    public final Industry industry;
    
    public final ExistingLoans existingLoans;
    
    public final BalanceSheet balanceSheet;
    
    public final IncomeStatementData incomeStatementData;

    public Client(String name, Industry industry, BalanceSheet balanceSheet, IncomeStatementData incomeStatementData, ExistingLoans existingLoans) {
        this.name = name;
        this.industry = industry;
        this.balanceSheet = balanceSheet;
        this.incomeStatementData = incomeStatementData;
        this.existingLoans = existingLoans;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static Client createDefault() {
        return new Client("", Industry.AUTOMOTIVE, BalanceSheet.createDefault(), IncomeStatementData.createDefault(2016), ExistingLoans.createEmpty());
    }
    
}
