package hu.lae.infrastructure.ui.client;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.riskparameters.Industry;
import hu.lae.infrastructure.ui.client.balancesheet.BalanceSheetPanel;
import hu.lae.infrastructure.ui.client.existingloans.ExistingLoansPanel;
import hu.lae.infrastructure.ui.client.incomestatement.IncomeStatementPanel;

@SuppressWarnings("serial")
public class ClientPanel extends Panel {

    private final TextField nameField = new TextField("Client name");
    
    private final ComboBox<Industry> industryCombo = new ComboBox<>("Industry", Arrays.asList(Industry.values()));
    
    private final BalanceSheetPanel balanceSheetPanel;
    
    private final IncomeStatementPanel incomeStatementPanel;
    
    private final ExistingLoansPanel existingLoansPanel;
    
    public ClientPanel(Client client) {
        
        nameField.setValue(client.name);
        industryCombo.setValue(client.industry);
        balanceSheetPanel = new BalanceSheetPanel(client.balanceSheet);
        incomeStatementPanel = new IncomeStatementPanel(client.incomeStatementData);
        existingLoansPanel = new ExistingLoansPanel(client.existingLoans);
        
        setContent(createLayout());
    }
    
    private Layout createLayout() {
        
        setCaption("Client data");
        addStyleName("colored");
        
        nameField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        nameField.setPlaceholder("PBT Kft");
        
        industryCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        industryCombo.setEmptySelectionAllowed(false);
        industryCombo.setItemCaptionGenerator(i -> i.displayName);
        
        HorizontalLayout row1 = new HorizontalLayout(nameField, industryCombo);
        HorizontalLayout row2 = new HorizontalLayout(balanceSheetPanel, incomeStatementPanel);
        VerticalLayout mainLayout = new VerticalLayout(row1, row2, existingLoansPanel);
        
        return mainLayout;
    }

    public Client getClient() {
        return new Client(nameField.getValue(), industryCombo.getValue(), balanceSheetPanel.getBalanceSheet(), incomeStatementPanel.getIncomeStatements(), existingLoansPanel.getExistingLoans());
    }
    
}

