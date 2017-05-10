package hu.lae.infrastructure.ui.loancalculation;

import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.Client;
import hu.lae.accounting.FreeCashFlowCalculator;
import hu.lae.loan.LoanCalculator;
import hu.lae.loan.LoanRequest;

@SuppressWarnings("serial")
public class LoanHelperWindow extends Window {

    private final LoanSelector loanSelector;
    
    public LoanHelperWindow(LoanCalculator loanCalculator, Client client, int paybackYears, FreeCashFlowCalculator freeCashFlowCalculator, LoanRequest loanRequest, boolean refinanceExistingLongTermLoans, Consumer<LoanRequest> action) {
        setCaption("Loan selection helper");
        setModal(true);
        
        loanSelector = new LoanSelector(loanCalculator, client, paybackYears, freeCashFlowCalculator, loanRequest, refinanceExistingLongTermLoans);
        
        Button button = new Button("Use the selected values");
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        button.addClickListener(click -> {
            action.accept(loanSelector.getValue());
            close();
        });
        
        VerticalLayout layout = new VerticalLayout(loanSelector, button);
        layout.setComponentAlignment(button, Alignment.BOTTOM_CENTER);
        
        setContent(layout);
    }
    
}
