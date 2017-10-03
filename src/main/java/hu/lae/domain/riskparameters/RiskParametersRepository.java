package hu.lae.domain.riskparameters;

public interface RiskParametersRepository {

    RiskParameters loadRiskParameters();
    
    void updateRiskParameters(RiskParameters riskParameters);
    
}
