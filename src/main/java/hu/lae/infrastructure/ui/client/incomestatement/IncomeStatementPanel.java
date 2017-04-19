package hu.lae.infrastructure.ui.client.incomestatement;

import com.vaadin.shared.ui.MarginInfo;
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
        layout.setMargin(new MarginInfo(false, true, true, true));
        //layout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
        //addStyleName("colored");
        setContent(layout);
    }
    
    public IncomeStatement getIncomeStatement() {
        return form.getIncomeStatement();
    }
    
}
