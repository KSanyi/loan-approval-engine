package hu.lae.domain.accounting;

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
    
    public int year() {
        return year;
    }

}
