package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.infrastructure.ui.component.Button;

@SuppressWarnings("serial")
public class LoanHelperWindow extends Window {

    private final LoanSelector loanSelector;
    
    public LoanHelperWindow(LoanCalculator loanCalculator, Client client, int paybackYears, FreeCashFlowCalculator freeCashFlowCalculator, LoanRequest loanRequest, ExistingLoansRefinancing existingLoanRefinancing, Consumer<LoanRequest> action) {
        setCaption("Loan selection helper");
        setModal(true);
        
        loanSelector = new LoanSelector(loanCalculator, client, paybackYears, freeCashFlowCalculator, loanRequest, existingLoanRefinancing);
        
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
        
        addShortcutListener(VaadinUtil.createErrorSubmissionShortcutListener());
    }
    
    public void open() {
        UI.getCurrent().addWindow(this);
    }
    
}
