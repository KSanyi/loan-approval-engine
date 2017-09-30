package hu.lae.infrastructure.server;

import hu.lae.domain.industry.IndustryDataRepository;
import hu.lae.domain.riskparameters.RiskParameterRepository;
import hu.lae.usermanagement.Authenticator;

public class ApplicationService {

    public final Authenticator authenticator;
    
    public final RiskParameterRepository riskParameterRepository;
    
    public final IndustryDataRepository industryDataRepository;

    public ApplicationService(Authenticator authenticator, RiskParameterRepository riskParameterRepository, IndustryDataRepository industryDataRepository) {
        this.authenticator = authenticator;
        this.riskParameterRepository = riskParameterRepository;
        this.industryDataRepository = industryDataRepository;
    }
    
}
