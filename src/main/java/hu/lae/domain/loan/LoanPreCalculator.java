package hu.lae.domain.loan;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.Client;
import hu.lae.domain.industry.IndustryData;
import hu.lae.domain.legal.LegalEvaluationResult;
import hu.lae.domain.legal.LegalParameters;
import hu.lae.domain.riskparameters.RiskParameters;

public class LoanPreCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final RiskParameters riskParameters;
    
    public final LegalParameters legalParameters;
    
    public final IndustryData industryData;
    
    public LoanPreCalculator(RiskParameters riskParameters, LegalParameters legalParameters, IndustryData industryData) {
        this.riskParameters = riskParameters;
        this.legalParameters = legalParameters;
        this.industryData = industryData;
    }

    public int calculateMaxLoanDuration(Client client, double loanIncrement) {
    	
    	logger.debug("Calculating max loan duration");
    	
    	int maxLoanDurationForIndustry = riskParameters.industryMaxLoanDurations.maxLoanDuration(client.industry);
    	logger.debug("Max loan duration for industry: " + maxLoanDurationForIndustry);
    	
    	double ownEquityRatio = client.financialStatementData().balanceSheet.liabilities.equityRatio(loanIncrement);
    	double ownEquityRatioIndustryAverage = industryData.ownEquityRatioAverage(client.industry);
		Optional<Integer> maxLoanDurationCapByOwnEquity = riskParameters.thresholds.ownEquityRatioThresholds.maxLoanDuration(ownEquityRatio, ownEquityRatioIndustryAverage);
		logger.debug("Max loan duration cap by own equity: " + maxLoanDurationCapByOwnEquity);
				
    	LegalEvaluationResult legalEvaluationResult = legalParameters.evaluate(client.legalData);
    	Optional<Integer> maxLoanDurationCapByLegal = legalEvaluationResult.maxLoanDuration;
    	logger.debug("Max loan duration cap by legal: " + maxLoanDurationCapByLegal);
    	
    	int maxLoanDuration = min(maxLoanDurationForIndustry,
    			maxLoanDurationCapByOwnEquity.orElse(maxLoanDurationForIndustry),
    			maxLoanDurationCapByLegal.orElse(maxLoanDurationForIndustry));
    	logger.debug("Max Loan Duration: " + maxLoanDuration);
    	
    	return maxLoanDuration;
    }
    
    private static int min(int ... values) {
    	return IntStream.of(values).min().getAsInt();
    }
    
}
