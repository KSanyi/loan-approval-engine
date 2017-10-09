package hu.lae.domain.validation;

import java.text.DecimalFormat;

import hu.lae.domain.finance.FinancialStatementData;

public class EquityRatioValidator {

    private final static DecimalFormat DF = new DecimalFormat("0.00");
    
    private final double threshold;
    
    public EquityRatioValidator(double threshold) {
        this.threshold = threshold;
    }

    public ValidationResult validate(FinancialStatementData financialStatement) {
        
        double equityRatio = financialStatement.balanceSheet.liabilities.equityRatio();
        
        if(equityRatio < threshold) {
            return ValidationResult.Ko("Equity ratio (" + DF.format(equityRatio) + ") is below threshold (" + threshold + ")");
        }
        
        return ValidationResult.Ok();
    }
    
}
