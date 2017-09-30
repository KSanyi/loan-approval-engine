package hu.lae.infrastructure.ui.parameters.riskparameters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.riskparameters.CollateralRequirement;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.infrastructure.ui.component.PercentField;
import hu.lae.util.Pair;

@SuppressWarnings("serial")
public class CollateralRequirementsForm extends CustomField<CollateralRequirement> {

    private GridLayout layout = new GridLayout(7, 3);
    
    public CollateralRequirementsForm(CollateralRequirement collateralRequirement) {
        for(double limit : collateralRequirement.map.keySet()) {
            CollateralRequirement.Entry entry = collateralRequirement.map.get(limit);
            PercentField pdField = new PercentField("PD limit");
            pdField.setWidth("50px");
            pdField.setPercent(limit);
            AmountField amountLimitField = new AmountField("Amount");
            amountLimitField.setWidth("70px");
            amountLimitField.setAmount(entry.amountThreshold);
            PercentField dcField = new PercentField("DC");
            dcField.setPercent(entry.dcThreshold);
            Label label = new Label(entry.name + " ");
            label.addStyleName(ValoTheme.LABEL_SMALL);
            if(limit > 0.000001) {
                Arrays.asList(pdField, amountLimitField, dcField).stream().forEach(f -> f.setCaption(""));
            }
            layout.addComponents(label, gap(), pdField, gap(), amountLimitField, gap(), dcField);
            layout.setComponentAlignment(label, Alignment.BOTTOM_RIGHT);
        }
        layout.setMargin(true);
    }
    
    private static Component gap() {
        Label label = new Label("");
        label.setWidth("10px");
        return label;
    }
    
    @Override
    public CollateralRequirement getValue() {
        Map<Double, Pair<Long, Double>> entryMap = new HashMap<>();
        
        for(int i=0;i<3;i++) {
            double pdLimit = ((PercentField)layout.getComponent(2, i)).getPercent();
            long amount = ((AmountField)layout.getComponent(4, i)).getAmount();
            double dc = ((PercentField)layout.getComponent(6, i)).getPercent();
            entryMap.put(pdLimit, new Pair<>(amount, dc));
        }
        
        return new CollateralRequirement(entryMap);
    }

    @Override
    protected Component initContent() {
        Panel panel = new Panel("Collaterals", layout);
        panel.addStyleName("colored");
        
        return panel;
    }

    @Override
    protected void doSetValue(CollateralRequirement value) {
        throw new IllegalStateException();
    }

}
