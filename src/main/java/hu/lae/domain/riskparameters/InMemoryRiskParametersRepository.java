package hu.lae.domain.riskparameters;

import java.util.Map;

import hu.lae.domain.finance.YearlyData;
import hu.lae.domain.industry.Industry;
import hu.lae.util.Pair;

public class InMemoryRiskParametersRepository implements RiskParametersRepository {

    private RiskParameters riskParameters;
    
    public InMemoryRiskParametersRepository() {
        riskParameters = new RiskParameters("", "",
                0.4, new Haircuts(0.8, 0.5, 0.8, 0.4),
                new InterestRates(0.03, 0.05),
                new IndustryMaxLoanDurations(Map.of(Industry.CONSTRUCTION, 2, Industry.AUTOMOTIVE, 5)),
                1.2,
                new Thresholds(0.2, 1.2, 0.15, 0.75, 0.8, new OwnEquityRatioThresholds(0.7, 3, 0.5, 1)),
                new CollateralRequirement(Map.of(
                        0.0, new Pair<>(50L, 0.7), 
                        0.02, new Pair<>(30L, 0.4), 
                        0.04, new Pair<>(0L, 0.0))),
                new EbitdaCorrectionParameters(0.3, 0.1, -0.1, -0.05, new YearlyData<>(0.4, 0.35, 0.25))
                );
    }
    
    @Override
    public RiskParameters loadRiskParameters() {
        return riskParameters;
    }

    @Override
    public void updateRiskParameters(RiskParameters riskParameters) {
        this.riskParameters = riskParameters;
    }

}
