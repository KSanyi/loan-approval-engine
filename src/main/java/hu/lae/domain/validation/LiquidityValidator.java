package hu.lae.domain.validation;

import java.lang.invoke.MethodHandles;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.finance.FinancialStatementData;
import hu.lae.domain.loan.LoanRequest;

public class LiquidityValidator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final static DecimalFormat DF = new DecimalFormat("0.00");
    
    private final double threshold;
    
    private final double existingShortTermLoan;
    
    public LiquidityValidator(double existingShortTermLoan, double threshold) {
        this.existingShortTermLoan = existingShortTermLoan;
        this.threshold = threshold;
    }

    // why is it not used?
    public ValidationResult validate(FinancialStatementData financialStatement, LoanRequest loanRequest) {
        
        double shortTermLoans = existingShortTermLoan + loanRequest.shortTermLoan;
        double liquidityRatio1 = financialStatement.balanceSheet.liquidityRatio1(shortTermLoans);
        double liquidityRatio2 = financialStatement.balanceSheet.liquidityRatio2();
        double liquidityRatio3 = financialStatement.balanceSheet.liquidityRatio3();
        
        logger.debug("liquidityRatio1: " + liquidityRatio1);
        logger.debug("liquidityRatio2: " + liquidityRatio2);
        logger.debug("liquidityRatio3: " + liquidityRatio3);
        
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
    
    public ValidationResult validateRatio1(FinancialStatementData financialStatement, LoanRequest loanRequest) {
        double shortTermLoans = existingShortTermLoan + loanRequest.shortTermLoan;
        double liquidityRatio1 = financialStatement.balanceSheet.liquidityRatio1(shortTermLoans);
        logger.debug("liquidityRatio1: " + liquidityRatio1);
        if(liquidityRatio1 <  threshold) {
            return ValidationResult.Warning("Liquidity ratio 1 is too low: " + DF.format(liquidityRatio1) + " < " + threshold);
        } else {
            return ValidationResult.Ok();
        }
    }
    
    public ValidationResult validateRatio2(FinancialStatementData financialStatement) {
        double liquidityRatio2 = financialStatement.balanceSheet.liquidityRatio2();
        logger.debug("liquidityRatio2: " + liquidityRatio2);
        if(liquidityRatio2 <  threshold) {
            return ValidationResult.Warning("Liquidity ratio 2 is too low: " + DF.format(liquidityRatio2) + " < " + threshold);
        } else {
            return ValidationResult.Ok();
        }
    }
    
    public ValidationResult validateRatio3(FinancialStatementData financialStatement) {
        double liquidityRatio3 = financialStatement.balanceSheet.liquidityRatio3();
        logger.debug("liquidityRatio3: " + liquidityRatio3);
        if(liquidityRatio3 <  threshold) {
            return ValidationResult.Warning("Liquidity ratio 3 is too low: " + DF.format(liquidityRatio3) + " < " + threshold);
        } else {
            return ValidationResult.Ok();
        }
    }
    
}
