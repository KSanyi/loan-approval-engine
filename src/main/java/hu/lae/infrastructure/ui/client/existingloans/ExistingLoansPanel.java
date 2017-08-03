package hu.lae.infrastructure.ui.client.existingloans;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.domain.loan.ExistingLoans;

@SuppressWarnings("serial")
public class ExistingLoansPanel extends Panel {

    private final ExistingLoansForm form;
    
    public ExistingLoansPanel(ExistingLoans existingLoans) {
        setCaption("Existing loans");
        form = new ExistingLoansForm(existingLoans);
        
        setContent(new VerticalLayout(form));
    }
    
    public ExistingLoans getExistingLoans() {
        return form.getValue();
    }
    
}
