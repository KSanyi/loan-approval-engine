package hu.lae.infrastructure.ui.parameters.riskparameters;

import com.vaadin.ui.FormLayout;

import hu.lae.domain.riskparameters.Haircuts;
import hu.lae.infrastructure.ui.component.PercentField;

@SuppressWarnings("serial")
public class HaircutsForm extends FormLayout {

    private final PercentField arField = new PercentField("A/R justifiable ratio");
    private final PercentField stockField = new PercentField("Stock justifiable ratio");
    private final PercentField cashField = new PercentField("Cash justifiable ratio");
    private final PercentField otherField = new PercentField("Other justifiable ratio");

    public HaircutsForm(Haircuts haircuts) {
        setMargin(true);
        
        addComponents(arField, stockField, cashField, otherField);
        
        arField.setPercent(haircuts.accountsReceivable);
        stockField.setPercent(haircuts.stock);
        cashField.setPercent(haircuts.cash);
        otherField.setPercent(haircuts.other);
    }
    
    public Haircuts getHaircuts() {
        return new Haircuts(arField.getPercent(),
                stockField.getPercent(),
                cashField.getPercent(),
                otherField.getPercent());
    }
    
    
    
}
