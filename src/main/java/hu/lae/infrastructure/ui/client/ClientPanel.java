package hu.lae.infrastructure.ui.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.Client;
import hu.lae.domain.finance.FinancialHistory;
import hu.lae.domain.finance.FinancialStatementData;
import hu.lae.domain.industry.Industry;
import hu.lae.infrastructure.ui.client.existingloans.ExistingLoansPanel;
import hu.lae.infrastructure.ui.client.finstatement.FinancialHistoryWindow;
import hu.lae.infrastructure.ui.client.finstatement.FinancialStatementForm;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class ClientPanel extends Panel {

    private final TextField nameField = new TextField("Client name");
    
    private final ComboBox<Industry> industryCombo = new ComboBox<>("Industry", Arrays.asList(Industry.values()));
    
    private final Button historyButton = new Button("History", click -> showHistory());
    
    private final FinancialStatementForm financialStatementForm;
    
    private final ExistingLoansPanel existingLoansPanel;
    
    private final FinancialHistoryWindow financialHistoryWindow;
    
    private final PercentField pdField = new PercentField("PD");
    
    public ClientPanel(Client client) {
        
        nameField.setValue(client.name);
        industryCombo.setValue(client.industry);
        financialStatementForm = new FinancialStatementForm(client.financialStatementData());
        existingLoansPanel = new ExistingLoansPanel(client.existingLoans);
        financialHistoryWindow = new FinancialHistoryWindow(client.financialHistory);
        pdField.setPercent(client.pd);
        
        setContent(createLayout());
    }
    
    private void showHistory() {
        financialHistoryWindow.open();
    }

    private Layout createLayout() {
        
        setCaption("Client data");
        addStyleName("colored");
        
        nameField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        nameField.setPlaceholder("PBT Kft");
        
        industryCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        industryCombo.setEmptySelectionAllowed(false);
        industryCombo.setItemCaptionGenerator(i -> i.displayName);
        
        historyButton.addStyleName(ValoTheme.BUTTON_LINK);
        historyButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        HorizontalLayout row1 = new HorizontalLayout(nameField, industryCombo, historyButton, pdField);
        row1.setWidth("805px");
        row1.setComponentAlignment(pdField, Alignment.MIDDLE_RIGHT);
        HorizontalLayout row2 = new HorizontalLayout(financialStatementForm, existingLoansPanel);
        VerticalLayout mainLayout = new VerticalLayout(row1, row2);
        
        return mainLayout;
    }

    public Client getClient() {
        List<FinancialStatementData> financialStatements = new ArrayList<>(financialHistoryWindow.getFinancialHistory().financialStatements);
        financialStatements.add(financialStatementForm.getValue());
        FinancialHistory financialHistory = new FinancialHistory(financialStatements);
        return new Client(nameField.getValue(), industryCombo.getValue(), financialHistory, existingLoansPanel.getExistingLoans(), pdField.getPercent());
    }
    
}

