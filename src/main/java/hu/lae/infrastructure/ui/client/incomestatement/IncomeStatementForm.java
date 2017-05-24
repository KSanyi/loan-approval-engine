package hu.lae.infrastructure.ui.client.incomestatement;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.accounting.IncomeStatement;
import hu.lae.infrastructure.ui.component.AmountField;

@SuppressWarnings("serial")
public class IncomeStatementForm extends FormLayout {

    final Integer year;
    
    private final AmountField ebitdaField = new AmountField(null, "ebitda");
    private final AmountField salesField = new AmountField(null, "sales");
    private final AmountField amortizationField = new AmountField(null, "amortization");
    private final AmountField taxField = new AmountField(null, "tax");
    private final AmountField materialExpendituresField = new AmountField(null, "materialExpenditures");
    
    IncomeStatementForm(IncomeStatement incomeStatement) {
        
        salesField.setAmount(incomeStatement.sales);
        ebitdaField.setAmount(incomeStatement.ebitda);
        amortizationField.setAmount(incomeStatement.amortization);
        taxField.setAmount(incomeStatement.taxes);
        materialExpendituresField.setAmount(incomeStatement.materialExpenditures);
        
        this.year = incomeStatement.year;
        setSpacing(false);
        setMargin(false);
        Label yearLabel = new Label(year.toString());
        yearLabel.addStyleName(ValoTheme.LABEL_H4);
        addComponents(yearLabel, salesField, ebitdaField, amortizationField, taxField);
    }

    public IncomeStatement getIncomeStatement() {
        return new IncomeStatement(year, salesField.getAmount(), ebitdaField.getAmount(), amortizationField.getAmount(), taxField.getAmount(), materialExpendituresField.getAmount());
    }
    
}
