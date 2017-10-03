package hu.lae.domain.finance;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FinancialStatementData {

    public final int year;
    
    public final BalanceSheet balanceSheet;
    
    public final IncomeStatement incomeStatement;

    public FinancialStatementData(int year, BalanceSheet balanceSheet, IncomeStatement incomeStatement) {
        this.year = year;
        this.balanceSheet = balanceSheet;
        this.incomeStatement = incomeStatement;
    }
    
    public double supplierDays() {
        if(incomeStatement.materialExpenditures == 0) {
            return 0;
        }
        return (double)balanceSheet.liabilities.accountsPayable / incomeStatement.materialExpenditures * 365;
    }
    
    public double buyersDays() {
        if(incomeStatement.sales == 0) {
            return 0;
        }
        return (double)balanceSheet.assets.accountsReceivable / incomeStatement.sales * 365;
    }
    
    public double stockDays() {
        if(incomeStatement.sales == 0) {
            return 0;
        }
        return (double)balanceSheet.assets.stock / incomeStatement.sales * 365;
    }
    
    public int year() {
        return year;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
