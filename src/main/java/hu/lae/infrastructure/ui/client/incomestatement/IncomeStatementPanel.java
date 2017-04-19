package hu.lae.infrastructure.ui.client.incomestatement;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.accounting.IncomeStatementData;

@SuppressWarnings("serial")
public class IncomeStatementPanel extends Panel {

    private final List<IncomeStatementForm> forms;
    
    public IncomeStatementPanel(IncomeStatementData incomeStatementData) {
        setCaption("Income Statement");
        
        forms = incomeStatementData.incomeStatements()
                    .map(incomeStatement -> new IncomeStatementForm(incomeStatement))
                    .collect(Collectors.toList());
        
        VerticalLayout labels = new VerticalLayout(new Label(""), new Label("EBITDA"), new Label("Amortization"), new Label("Taxes"));
        labels.setMargin(false);
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
        layout.addComponent(labels);
        layout.addComponents(forms.toArray(new IncomeStatementForm[0]));
        layout.setMargin(new MarginInfo(false, true, true, true));
        setContent(layout);
    }
    
    public IncomeStatementData getIncomeStatements() {
        return new IncomeStatementData(forms.stream().map(IncomeStatementForm::getIncomeStatement).collect(Collectors.toList()));
    }
    
}
