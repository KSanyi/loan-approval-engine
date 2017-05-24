package hu.lae.domain.riskparameters;

public interface RiskParameterRepository {

    RiskParameters loadRiskParameters();
    
    void updateRiskParameters(RiskParameters riskParameters);
    
}
