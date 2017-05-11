package hu.lae.infrastructure.ui.client.existingloans;

import java.time.LocalDate;

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

    private final AmountField shortTermLoansField = new AmountField("Short term loans", "existing st loan");
    private final AmountField longTermLoansField = new AmountField("Long term loans", "existing lt loan");
    private final AmountField bulletField = new AmountField("Bullet", "bullet");
    private final DateField expiryField = new DateField("Expiry");
    
    ExistingLoansForm(ExistingLoans existingLoans) {
        addComponents(createLayout(existingLoans));
    }
    
    private Layout createLayout(ExistingLoans existingLoans) {
        
        shortTermLoansField.setAmount(existingLoans.shortTermLoans);
        longTermLoansField.setAmount(existingLoans.longTermLoans);
        bulletField.setAmount(existingLoans.bullet);
        expiryField.setValue(existingLoans.expiry);
        
        expiryField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        expiryField.setWidth("120px");
        expiryField.setRangeStart(Clock.date().plusYears(1));
        expiryField.addValueChangeListener(e -> {
            LocalDate firstValidDate = Clock.date().plusYears(1);
            if(e.getValue() == null || e.getValue().isBefore(firstValidDate)) {
                expiryField.setRangeStart(firstValidDate);
                expiryField.setValue(firstValidDate);
            }
        });
        
        FormLayout layout = new FormLayout(shortTermLoansField, longTermLoansField, bulletField, expiryField);
        layout.setSpacing(false);
        layout.setMargin(false);

        return layout;
    }
    
    public ExistingLoans getExistingLoans() {
        return new ExistingLoans(shortTermLoansField.getAmount(), longTermLoansField.getAmount(), expiryField.getValue(),
                bulletField.getAmount());
    }
    
}
