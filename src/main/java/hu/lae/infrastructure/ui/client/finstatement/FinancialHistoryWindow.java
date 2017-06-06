package hu.lae.infrastructure.ui.client.finstatement;

import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.accounting.FinancialHistory;

@SuppressWarnings("serial")
public class FinancialHistoryWindow extends Window {

    private final ComboBox<Integer> yearCombo;

    private final Button saveButton = new Button("Save", click -> save());

    private Map<Integer, FinancialStatementForm> financialStatementForms;

    private final Layout actualFormHolder = new HorizontalLayout();

    public FinancialHistoryWindow(FinancialHistory financialStatementHistory) {

        setModal(true);
        setCaption("Financial statements");
        
        int latestYear = financialStatementHistory.lastFinancialStatementData().year;

        financialStatementForms = financialStatementHistory.financialStatements.stream()
                .filter(f -> f.year < latestYear)
                .collect(Collectors.toMap(f -> f.year, f -> new FinancialStatementForm(f)));

        yearCombo = new ComboBox<>("Year", financialStatementForms.keySet());
        yearCombo.addValueChangeListener(v -> showFinancialStatement(v.getValue()));
        
        yearCombo.setValue(latestYear-1);
        
        createLayout();
    }

    private void createLayout() {

        yearCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        yearCombo.setWidth("80px");
        yearCombo.setEmptySelectionAllowed(false);

        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);

        VerticalLayout layout = new VerticalLayout(yearCombo, actualFormHolder, saveButton);
        layout.setComponentAlignment(yearCombo, Alignment.TOP_CENTER);
        layout.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        
        layout.setMargin(new MarginInfo(false, true, true, true));

        setContent(layout);
    }

    private void showFinancialStatement(Integer value) {
        actualFormHolder.removeAllComponents();
        actualFormHolder.addComponent(financialStatementForms.get(value));
    }

    private void save() {
        close();
    }

    public void open() {
        UI.getCurrent().addWindow(this);
        center();
    }
    
    public FinancialHistory getFinancialHistory() {
        return new FinancialHistory(financialStatementForms.values().stream().map(f -> f.getValue()).collect(Collectors.toList()));
    }

}
