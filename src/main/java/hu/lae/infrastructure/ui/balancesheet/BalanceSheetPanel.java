package hu.lae.infrastructure.ui.balancesheet;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.BalanceSheet;

@SuppressWarnings("serial")
public class BalanceSheetPanel extends Panel {

    private final Button updateButton = new Button("Update");
    
    private final BalanceSheetForm form;
    
    public BalanceSheetPanel(BalanceSheet balanceSheet) {
        setCaption("Balance Sheet");
        form = new BalanceSheetForm(balanceSheet);
        
        updateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        updateButton.addStyleName(ValoTheme.BUTTON_SMALL);
        VerticalLayout layout = new VerticalLayout(form);
        layout.setMargin(true);
        //layout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
        addStyleName("colored");
        setContent(layout);
    }
    
    public BalanceSheet getBalanceSheet() {
        return form.getBalanceSheet();
    }
    
}
