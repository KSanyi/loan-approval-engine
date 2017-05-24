package hu.lae.infrastructure.ui.client.validation;

import java.text.DecimalFormat;

import hu.lae.domain.Client;
import hu.lae.domain.loan.LoanRequest;

public class LiquidityValidator implements Validator {

    private final static DecimalFormat DF = new DecimalFormat("0.00");
    
    private final double threshold;
    
    public LiquidityValidator(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public ValidationResult validate(Client client, LoanRequest loanRequest) {
        
        double shortTermLoans = client.existingLoans.shortTermLoans + loanRequest.shortTermLoan;
        double liquidityRatio1 = client.balanceSheet.liquidityRatio1(shortTermLoans);
        double liquidityRatio2 = client.balanceSheet.liquidityRatio2();
        double liquidityRatio3 = client.balanceSheet.liquidityRatio3();
        
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
