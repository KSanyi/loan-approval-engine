package hu.lae.riskparameters;

public interface RiskParameterRepository {

    RiskParameters loadRiskParameters();
    
    void updateRiskParameters(RiskParameters riskParameters);
    
}
