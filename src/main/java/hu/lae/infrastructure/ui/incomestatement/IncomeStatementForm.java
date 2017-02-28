package hu.lae.infrastructure.ui.incomestatement;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

import hu.lae.accounting.IncomeStatement;
import hu.lae.infrastructure.ui.component.AmountField;

@SuppressWarnings("serial")
public class IncomeStatementForm extends FormLayout {

    final Integer year;
    
    private final AmountField ebitdaField = new AmountField("EBITDA");
    private final AmountField amortizationField = new AmountField("Amortization");
    private final AmountField taxField = new AmountField("Taxes");
    
    IncomeStatementForm(IncomeStatement incomeStatement) {
        this.year = incomeStatement.year;
        setSpacing(false);
        setMargin(false);
        Label yearLabel = new Label(year.toString());
        //yearLabel.addStyleName(ValoTheme);
        addComponents(yearLabel, ebitdaField, amortizationField, taxField);
        
        //setComponentAlignment(yearLabel, Alignment.TOP_LEFT);
    }

    public IncomeStatement getIncomeStatement() {
        return new IncomeStatement(year, ebitdaField.getAmount(), amortizationField.getAmount(), taxField.getAmount());
    }
    
}
