package hu.lae.infrastructure.ui.loancalculation;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.Client;
import hu.lae.accounting.FreeCashFlowCalculator;
import hu.lae.loan.LoanCalculator;

@SuppressWarnings("serial")
public class LoanHelperWindow extends Window {

    private final LoanSelector loanSelector;
    
    public LoanHelperWindow(LoanCalculator loanCalculator, Client client, int paybackYears, FreeCashFlowCalculator freeCashFlowCalculator) {
        setCaption("Loan selection helper");
        setModal(true);
        
        loanSelector = new LoanSelector(loanCalculator, client, paybackYears, freeCashFlowCalculator);
        
        Button button = new Button("Use the selected values");
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        
        VerticalLayout layout = new VerticalLayout(loanSelector, button);
        layout.setComponentAlignment(button, Alignment.BOTTOM_CENTER);
        
        setContent(layout);
    }
    
}
