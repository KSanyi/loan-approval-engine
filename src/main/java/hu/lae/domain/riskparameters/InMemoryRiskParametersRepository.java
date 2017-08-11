package hu.lae.domain.riskparameters;

import hu.lae.util.MapFactory;
import hu.lae.util.Pair;

public class InMemoryRiskParametersRepository implements RiskParameterRepository {

    private RiskParameters riskParameters;
    
    public InMemoryRiskParametersRepository() {
        riskParameters = new RiskParameters("", "",
                0.4, new Haircuts(0.8, 0.5, 0.8, 0.4),
                new InterestRate(0.03),
                new MaxLoanDurations(MapFactory.of(Industry.CONSTRUCTION, 2, Industry.AUTOMOTIVE, 5)),
                new InterestRate(0.05),
                1.2,
                new Thresholds(0.2, 1.2, 0.15, 0.75, 0.8),
                new CollateralRequirement(MapFactory.of(
                        0.0, new Pair<>(50L, 0.7), 
                        0.02, new Pair<>(30L, 0.4), 
                        0.04, new Pair<>(0L, 0.0))));
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
