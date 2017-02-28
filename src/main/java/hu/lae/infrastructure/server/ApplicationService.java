package hu.lae.infrastructure.server;

import hu.lae.riskparameters.RiskParameterRepository;
import hu.lae.usermanagement.Authenticator;

public class ApplicationService {

    public final Authenticator authenticator;
    
    public final RiskParameterRepository riskParameterRepository;

    public ApplicationService(Authenticator authenticator, RiskParameterRepository riskParameterRepository) {
        this.authenticator = authenticator;
        this.riskParameterRepository = riskParameterRepository;
    }
    
}
