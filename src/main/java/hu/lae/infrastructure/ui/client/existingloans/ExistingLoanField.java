package hu.lae.infrastructure.ui.client.existingloans;

import java.time.LocalDate;
import java.util.Arrays;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.loan.ExistingLoan;
import hu.lae.domain.loan.LoanType;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.infrastructure.ui.component.CheckBox;
import hu.lae.infrastructure.ui.component.DateField;
import hu.lae.infrastructure.ui.component.RadioButtonGroup;
import hu.lae.util.Clock;

@SuppressWarnings("serial")
public class ExistingLoanField extends CustomField<ExistingLoan> {

    private final RadioButtonGroup<LoanType> loanTypeRadio = new RadioButtonGroup<>("", Arrays.asList(LoanType.values()));
    
    private final AmountField amountField = new AmountField("Amount");
    
    private final DateField expiryField = new DateField("Expiry");
    
    private final CheckBox isOwnCheckBox = new CheckBox("Own loan");
    
    public ExistingLoanField(ExistingLoan existingLoan) {
        
        loanTypeRadio.addValueChangeListener(v -> {
            if(v.getValue() == LoanType.LongTerm) {
                expiryField.setEnabled(true);
                expiryField.setValue(Clock.date().plusYears(1));
            } else {
                expiryField.setValue(null);
                expiryField.setEnabled(false);
            }
        });
        
        loanTypeRadio.setValue(existingLoan.expiry.isPresent() ? LoanType.LongTerm : LoanType.ShortTerm);
        amountField.setAmount(existingLoan.amount);
        expiryField.setValue(existingLoan.expiry.orElse(null));
        isOwnCheckBox.setValue(existingLoan.isOwn);
    }
    
    @Override
    public ExistingLoan getValue() {
        if(expiryField.getValue() == null) {
            return ExistingLoan.newShortTermLoan(amountField.getAmount(), isOwnCheckBox.getValue());
        } else {
            return ExistingLoan.newLongTermLoan(amountField.getAmount(), expiryField.getValue(), isOwnCheckBox.getValue());
        }
    }

    @Override
    protected Component initContent() {
        HorizontalLayout horizontalLayout = new HorizontalLayout(loanTypeRadio, amountField, expiryField, isOwnCheckBox);
        horizontalLayout.setComponentAlignment(amountField, Alignment.MIDDLE_CENTER);
        horizontalLayout.setComponentAlignment(expiryField, Alignment.MIDDLE_CENTER);
        horizontalLayout.setComponentAlignment(isOwnCheckBox, Alignment.MIDDLE_CENTER);
        
        isOwnCheckBox.addStyleName(ValoTheme.CHECKBOX_SMALL);
        
        loanTypeRadio.addStyleName(ValoTheme.CHECKBOX_SMALL);
        
        expiryField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        expiryField.setWidth("110px");
        expiryField.setRangeStart(Clock.date().plusYears(1));
        expiryField.addValueChangeListener(e -> {
            LocalDate firstValidDate = Clock.date().plusYears(1);
            if(e.getValue() == null || e.getValue().isBefore(firstValidDate)) {
                expiryField.setRangeStart(firstValidDate);
                expiryField.setValue(firstValidDate);
            }
        });
        return horizontalLayout;
    }

    @Override
    protected void doSetValue(ExistingLoan value) {
        throw new UnsupportedOperationException();
    }
    
}
