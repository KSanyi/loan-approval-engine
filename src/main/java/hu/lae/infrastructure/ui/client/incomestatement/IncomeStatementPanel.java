package hu.lae.infrastructure.ui.client.incomestatement;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.IncomeStatementData;

@SuppressWarnings("serial")
public class IncomeStatementPanel extends Panel {

    private final Button updateButton = new Button("Update");
    
    private final List<IncomeStatementForm> forms;
    
    public IncomeStatementPanel(IncomeStatementData incomeStatementData) {
        setCaption("Income Statement");
        
        forms = incomeStatementData.incomeStatements()
                    .map(incomeStatement -> new IncomeStatementForm(incomeStatement))
                    .collect(Collectors.toList());
        
        updateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        updateButton.addStyleName(ValoTheme.BUTTON_SMALL);
        VerticalLayout layout = new VerticalLayout(forms.toArray(new IncomeStatementForm[0]));
        layout.setMargin(new MarginInfo(false, true, true, true));
        //layout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
        //addStyleName("colored");
        setContent(layout);
    }
    
    public IncomeStatementData getIncomeStatements() {
        return new IncomeStatementData(forms.stream().map(IncomeStatementForm::getIncomeStatement).collect(Collectors.toList()));
    }
    
}
