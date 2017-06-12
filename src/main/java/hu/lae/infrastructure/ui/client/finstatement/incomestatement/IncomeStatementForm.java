package hu.lae.infrastructure.ui.client.finstatement.incomestatement;

import com.vaadin.ui.FormLayout;

import hu.lae.domain.accounting.IncomeStatement;
import hu.lae.infrastructure.ui.component.AmountField;

@SuppressWarnings("serial")
public class IncomeStatementForm extends FormLayout {

    private final AmountField salesField = new AmountField("Sales", "sales");
    private final AmountField ebitdaField = new AmountField("Ebitda", "ebitda");
    private final AmountField amortizationField = new AmountField("Amortization", "amortization");
    private final AmountField taxField = new AmountField("Tax", "tax");
    private final AmountField materialExpendituresField = new AmountField("Material exp", "materialExpenditures");
    
    IncomeStatementForm(IncomeStatement incomeStatement) {
        
        salesField.setAmount(incomeStatement.sales);
        ebitdaField.setAmount(incomeStatement.ebitda);
        amortizationField.setAmount(incomeStatement.amortization);
        taxField.setAmount(incomeStatement.taxes);
        materialExpendituresField.setAmount(incomeStatement.materialExpenditures);
        
        setSpacing(false);
        setMargin(false);
        addComponents(salesField, ebitdaField, materialExpendituresField, amortizationField, taxField);
    }

    public IncomeStatement getIncomeStatement() {
        return new IncomeStatement(salesField.getAmount(), ebitdaField.getAmount(), amortizationField.getAmount(), taxField.getAmount(), materialExpendituresField.getAmount());
    }
    
}
