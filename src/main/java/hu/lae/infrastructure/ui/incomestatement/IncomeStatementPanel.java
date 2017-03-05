package hu.lae.infrastructure.ui.incomestatement;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.IncomeStatement;

@SuppressWarnings("serial")
public class IncomeStatementPanel extends Panel {

    private final Button updateButton = new Button("Update");
    
    private final IncomeStatementForm form;
    
    public IncomeStatementPanel(IncomeStatement incomeStatement) {
        setCaption("Income Statement");
        form = new IncomeStatementForm(incomeStatement);
        
        updateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        updateButton.addStyleName(ValoTheme.BUTTON_SMALL);
        VerticalLayout layout = new VerticalLayout(form);
        layout.setMargin(true);
        //layout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
        addStyleName("colored");
        setContent(layout);
    }
    
    public IncomeStatement getIncomeStatement() {
        return form.getIncomeStatement();
    }
    
}
