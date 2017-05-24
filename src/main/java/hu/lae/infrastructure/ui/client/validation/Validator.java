package hu.lae.infrastructure.ui.client.validation;

import hu.lae.domain.Client;
import hu.lae.domain.loan.LoanRequest;

public interface Validator {

    ValidationResult validate(Client client, LoanRequest loanRequest);
    
}
