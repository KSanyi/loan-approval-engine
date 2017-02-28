package hu.lae.riskparameters;

import java.math.BigDecimal;

public class InMemoryRiskParametersRepository implements RiskParameterRepository {

    private RiskParameters riskParameters;
    
    public InMemoryRiskParametersRepository() {
        riskParameters = new RiskParameters("", "",
                new BigDecimal("0.4"), 
                new Haircuts(new BigDecimal("0.8"), new BigDecimal("0.5"), new BigDecimal("0.8"), new BigDecimal("0.4")),
                new InterestRate(new BigDecimal("0.03")),
                new InterestRate(new BigDecimal("0.03")),
                new BigDecimal("1.2"));
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
