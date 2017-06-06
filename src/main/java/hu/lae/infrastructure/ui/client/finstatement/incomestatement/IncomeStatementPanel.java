package hu.lae.infrastructure.ui.client.finstatement.incomestatement;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.domain.accounting.IncomeStatement;

@SuppressWarnings("serial")
public class IncomeStatementPanel extends Panel {

    private final IncomeStatementForm incomeStatementForm;
    
    public IncomeStatementPanel(IncomeStatement incomeStatement) {
        setCaption("Income Statement");
        incomeStatementForm = new IncomeStatementForm(incomeStatement);
        
        setContent(new VerticalLayout(incomeStatementForm));
    }
    
    public IncomeStatement getIncomeStatement() {
        return incomeStatementForm.getIncomeStatement();
    }
    
}
