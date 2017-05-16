package hu.lae.infrastructure.ui.client.balancesheet;

import com.vaadin.ui.Panel;

import hu.lae.accounting.BalanceSheet;

@SuppressWarnings("serial")
public class BalanceSheetPanel extends Panel {

    private final BalanceSheetForm form;
    
    public BalanceSheetPanel(BalanceSheet balanceSheet) {
        setCaption("Balance Sheet");
        form = new BalanceSheetForm(balanceSheet);
        
        setContent(form);
    }
    
    public BalanceSheet getBalanceSheet() {
        return form.getBalanceSheet();
    }
    
}
