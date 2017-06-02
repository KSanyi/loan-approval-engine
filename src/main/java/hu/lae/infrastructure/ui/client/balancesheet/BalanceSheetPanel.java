package hu.lae.infrastructure.ui.client.balancesheet;

import java.util.Arrays;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.accounting.BalanceSheet;

@SuppressWarnings("serial")
public class BalanceSheetPanel extends Panel {

    private final Button historyButton = new Button("History", click -> showHistory());
    
    private final BalanceSheetForm form;
    
    public BalanceSheetPanel(BalanceSheet balanceSheet) {
        setCaption("Balance Sheet");
        form = new BalanceSheetForm(balanceSheet);
        
        historyButton.addStyleName(ValoTheme.BUTTON_LINK);
        historyButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        VerticalLayout layout = new VerticalLayout(historyButton, form);
        layout.setMargin(false);
        layout.setSpacing(false);
        
        setContent(layout);
    }
    
    private void showHistory() {
        new BalanceSheetHistoryWindow(Arrays.asList(form.getValue())).open();
    }

    public BalanceSheet getBalanceSheet() {
        return form.getValue();
    }
    
}
