package hu.lae.infrastructure.ui.riskparameters;

import com.vaadin.ui.FormLayout;

import hu.lae.infrastructure.ui.component.PercentField;
import hu.lae.riskparameters.Haircuts;

@SuppressWarnings("serial")
public class HaircutsForm extends FormLayout {

    private final PercentField arField = new PercentField("A/R justifiable ratio");
    private final PercentField stockField = new PercentField("Stock justifiable ratio");
    private final PercentField cashField = new PercentField("Cash justifiable ratio");
    private final PercentField otherField = new PercentField("Other justifiable ratio");

    public HaircutsForm(Haircuts haircuts) {
        setMargin(true);
        
        addComponents(arField, stockField, cashField, otherField);
        
        arField.setNumber(haircuts.accountsReceivable);
        stockField.setNumber(haircuts.stock);
        cashField.setNumber(haircuts.cash);
        otherField.setNumber(haircuts.other);
    }
    
    public Haircuts getHaircuts() {
        return new Haircuts(arField.getPercent(),
                stockField.getPercent(),
                cashField.getPercent(),
                otherField.getPercent());
    }
    
    
    
}
