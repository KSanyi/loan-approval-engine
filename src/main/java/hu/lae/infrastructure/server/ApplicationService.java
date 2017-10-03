package hu.lae.infrastructure.server;

import hu.lae.domain.industry.IndustryDataRepository;
import hu.lae.domain.legal.LegalParametersRepository;
import hu.lae.domain.riskparameters.RiskParametersRepository;
import hu.lae.usermanagement.Authenticator;

public class ApplicationService {

    public final Authenticator authenticator;
    
    public final RiskParametersRepository riskParameterRepository;
    
    public final LegalParametersRepository legalParametersRepository;
    
    public final IndustryDataRepository industryDataRepository;

    public ApplicationService(Authenticator authenticator,
            RiskParametersRepository riskParameterRepository,
            LegalParametersRepository legalParametersRepository,
            IndustryDataRepository industryDataRepository) {
        this.authenticator = authenticator;
        this.riskParameterRepository = riskParameterRepository;
        this.legalParametersRepository = legalParametersRepository;
        this.industryDataRepository = industryDataRepository;
    }
    
}
