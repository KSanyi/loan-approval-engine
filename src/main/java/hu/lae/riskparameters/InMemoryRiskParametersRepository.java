package hu.lae.riskparameters;

import hu.lae.util.MapFactory;

public class InMemoryRiskParametersRepository implements RiskParameterRepository {

    private RiskParameters riskParameters;
    
    public InMemoryRiskParametersRepository() {
        riskParameters = new RiskParameters("", "",
                0.4, new Haircuts(0.8, 0.5, 0.8, 0.4),
                new InterestRate(0.03),
                new MaxLoanDurations(MapFactory.of(Industry.CONSTRUCTION, 2, Industry.AUTOMOTIVE, 5)),
                new InterestRate(0.05),
                1.2);
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
