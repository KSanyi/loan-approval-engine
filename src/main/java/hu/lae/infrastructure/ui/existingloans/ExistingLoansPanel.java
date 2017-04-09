package hu.lae.infrastructure.ui.existingloans;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.loan.ExistingLoans;

@SuppressWarnings("serial")
public class ExistingLoansPanel extends Panel {

    private final ExistingLoansForm form;
    
    public ExistingLoansPanel(ExistingLoans existingLoans) {
        setCaption("Existing loans");
        form = new ExistingLoansForm(existingLoans);
        
        VerticalLayout layout = new VerticalLayout(form);
        layout.setMargin(true);
        //layout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
        addStyleName("colored");
        setContent(layout);
    }
    
    public ExistingLoans getExistingLoans() {
        return form.getExistingLoans();
    }
    
}
