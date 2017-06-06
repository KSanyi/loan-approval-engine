package hu.lae.domain.validation;

import java.text.DecimalFormat;

import hu.lae.domain.accounting.FinancialStatementData;
import hu.lae.domain.loan.LoanRequest;

public class LiquidityValidator {

    private final static DecimalFormat DF = new DecimalFormat("0.00");
    
    private final double threshold;
    
    private final double existingShortTermLoan;
    
    public LiquidityValidator(double existingShortTermLoan, double threshold) {
        this.existingShortTermLoan = existingShortTermLoan;
        this.threshold = threshold;
    }

    public ValidationResult validate(FinancialStatementData financialStatement, LoanRequest loanRequest) {
        
        double shortTermLoans = existingShortTermLoan + loanRequest.shortTermLoan;
        double liquidityRatio1 = financialStatement.balanceSheet.liquidityRatio1(shortTermLoans);
        double liquidityRatio2 = financialStatement.balanceSheet.liquidityRatio2();
        double liquidityRatio3 = financialStatement.balanceSheet.liquidityRatio3();
        
        if(liquidityRatio2 < threshold) {
            return ValidationResult.Ko("Liquidity ratio2 (" + DF.format(liquidityRatio2) + ") is below threshold (" + threshold + ")");
        }
        
        if(liquidityRatio1 <  threshold) {
            return ValidationResult.Warning("Restructure short term loan. Liquidity ratio1 (" + DF.format(liquidityRatio1) + ") is below threshold (" + threshold + ")");
        }
        
        if(liquidityRatio3 <  threshold) {
            return ValidationResult.Warning("Confirm existance of cash. Liquidity ratio3 (" + DF.format(liquidityRatio3) + ") is below threshold (" + threshold + ")");
        }
        
        return ValidationResult.Ok();
    }
    
}
