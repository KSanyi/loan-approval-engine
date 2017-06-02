package hu.lae.infrastructure.ui.client.balancesheet;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.accounting.BalanceSheet;

@SuppressWarnings("serial")
class BalanceSheetHistoryWindow extends Window {

    private final ComboBox<Integer> yearCombo;
    
    private final BalanceSheetForm balanceSheetForm;
    
    private final Button saveButton = new Button("Save", click -> save());
    
    private final Map<Integer, BalanceSheet> balanceSheetMap;
    
    BalanceSheetHistoryWindow(List<BalanceSheet> balanceSheets) {
        
        setCaption("Balance sheet history");
        
        List<Integer> years = balanceSheets.stream().map(b -> b.year).sorted().collect(Collectors.toList());
        
        balanceSheetMap = balanceSheets.stream().collect(Collectors.toMap(b -> b.year, b -> b));
        
        yearCombo = new ComboBox<>("Year", years);
        yearCombo.addValueChangeListener(v -> showBalanceSheet(v.getValue()));
        
        balanceSheetForm = new BalanceSheetForm(balanceSheets.get(0));
        
        yearCombo.setValue(balanceSheetMap.keySet().iterator().next());
        
        createLayout();
    }
    
    private void save() {
        close();
    }

    private void showBalanceSheet(Integer value) {
        balanceSheetForm.setValue(balanceSheetMap.get(value));
    }

    private void createLayout() {
        yearCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        yearCombo.setWidth("80px");
        yearCombo.setEmptySelectionAllowed(false);
        
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        VerticalLayout layout = new VerticalLayout(yearCombo, balanceSheetForm, saveButton);
        layout.setComponentAlignment(yearCombo, Alignment.TOP_CENTER);
        layout.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        layout.setSpacing(false);
        setContent(layout);
        
        setModal(true);
    }
    
    void open() {
        UI.getCurrent().addWindow(this);
    }
    
}
