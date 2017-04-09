package hu.lae.infrastructure.ui.existingloans;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.loan.ExistingLoans;
import hu.lae.util.DateUtil;

@SuppressWarnings("serial")
class ExistingLoansForm extends HorizontalLayout {

    private final AmountField shortTermLoansField = new AmountField("Short term loans");
    private final AmountField longTermLoansField = new AmountField("Long term loans");
    private final AmountField bulletField = new AmountField("Bullet");
    private final DateField expirityField = new DateField("Expirity");
    private final CheckBox isToBeRefinancedCheckBox = new CheckBox("Is to be refinanced");
    
    ExistingLoansForm(ExistingLoans existingLoans) {
        setSpacing(true);
        addComponents(createLayout(existingLoans));
    }
    
    private Layout createLayout(ExistingLoans existingLoans) {
        
        shortTermLoansField.setAmount(existingLoans.shortTermLoans);
        longTermLoansField.setAmount(existingLoans.longTermLoans);
        bulletField.setAmount(existingLoans.bullet);
        expirityField.setValue(DateUtil.convertToDate(existingLoans.expirity));
        isToBeRefinancedCheckBox.setValidationVisible(existingLoans.isToBeRefinanced);
        
        expirityField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        expirityField.setWidth("120px");
        
        FormLayout layout = new FormLayout(shortTermLoansField, longTermLoansField, bulletField, expirityField, isToBeRefinancedCheckBox);
        layout.setSpacing(false);
        layout.setMargin(false);

        return layout;
    }
    
    public ExistingLoans getExistingLoans() {
        return new ExistingLoans(shortTermLoansField.getAmount(), longTermLoansField.getAmount(), DateUtil.convertToLocalDate(expirityField.getValue()),
                bulletField.getAmount(), isToBeRefinancedCheckBox.getValue());
    }
    
}
