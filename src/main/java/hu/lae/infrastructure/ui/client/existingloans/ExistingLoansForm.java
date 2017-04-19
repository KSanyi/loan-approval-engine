package hu.lae.infrastructure.ui.client.existingloans;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.loan.ExistingLoans;
import hu.lae.util.Clock;

@SuppressWarnings("serial")
class ExistingLoansForm extends HorizontalLayout {

    private final AmountField shortTermLoansField = new AmountField("Short term loans");
    private final AmountField longTermLoansField = new AmountField("Long term loans");
    private final AmountField bulletField = new AmountField("Bullet");
    private final DateField expirityField = new DateField("Expirity");
    private final CheckBox isToBeRefinancedCheckBox = new CheckBox("Is to be refinanced");
    
    ExistingLoansForm(ExistingLoans existingLoans) {
        addComponents(createLayout(existingLoans));
    }
    
    private Layout createLayout(ExistingLoans existingLoans) {
        
        shortTermLoansField.setAmount(existingLoans.shortTermLoans);
        longTermLoansField.setAmount(existingLoans.longTermLoans);
        bulletField.setAmount(existingLoans.bullet);
        expirityField.setValue(existingLoans.expirity);
        isToBeRefinancedCheckBox.setValue(existingLoans.isToBeRefinanced);
        
        expirityField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        expirityField.setWidth("120px");
        expirityField.setRangeStart(Clock.date());
        
        FormLayout layout = new FormLayout(shortTermLoansField, longTermLoansField, bulletField, expirityField, isToBeRefinancedCheckBox);
        layout.setSpacing(false);
        layout.setMargin(false);

        return layout;
    }
    
    public ExistingLoans getExistingLoans() {
        return new ExistingLoans(shortTermLoansField.getAmount(), longTermLoansField.getAmount(), expirityField.getValue(),
                bulletField.getAmount(), isToBeRefinancedCheckBox.getValue());
    }
    
}
