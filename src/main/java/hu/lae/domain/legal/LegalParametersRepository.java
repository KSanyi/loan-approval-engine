package hu.lae.domain.legal;

public interface LegalParametersRepository {

    LegalParameters loadLegalParameters();
    
    void updateLegalParameters(LegalParameters legalParameters);
    
}
