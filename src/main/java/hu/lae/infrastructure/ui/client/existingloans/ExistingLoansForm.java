package hu.lae.infrastructure.ui.client.existingloans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.loan.ExistingLoan;
import hu.lae.domain.loan.ExistingLoans;

@SuppressWarnings("serial")
class ExistingLoansForm extends CustomField<ExistingLoans> {
    
    private final VerticalLayout existingLoanFieldsContainer = new VerticalLayout();
    
    private final Button addLoanButton = new Button("Add loan", click -> addLoan(ExistingLoan.newShortTermLoan(0, false)));
    
    ExistingLoansForm(ExistingLoans existingLoans) {
        existingLoans.existingLoans.stream().forEach(this::addLoan);
    }
    
    private void addLoan(ExistingLoan existingLoan) {
        Button deleteButton = new Button(VaadinIcons.CLOSE);
        deleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
        deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
        HorizontalLayout wrapper = new HorizontalLayout(new ExistingLoanField(existingLoan), deleteButton);
        wrapper.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
        deleteButton.addClickListener(click -> existingLoanFieldsContainer.removeComponent(wrapper));
        existingLoanFieldsContainer.addComponent(wrapper);
    }

    @Override
    public ExistingLoans getValue() {
        List<ExistingLoan> existingLoans = new ArrayList<>();
        Iterator<Component> iterator = existingLoanFieldsContainer.iterator();
        while(iterator.hasNext()) {
            HorizontalLayout wrapper = (HorizontalLayout)iterator.next();
            ExistingLoanField existingLoanField = (ExistingLoanField)wrapper.getComponent(0);
            existingLoans.add(existingLoanField.getValue());
        }
        return new ExistingLoans(existingLoans);
    }

    @Override
    protected Component initContent() {
        
        existingLoanFieldsContainer.setSpacing(false);
        existingLoanFieldsContainer.setMargin(false);
        
        addLoanButton.addStyleName(ValoTheme.BUTTON_SMALL);
        addLoanButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        
        VerticalLayout verticalLayout = new VerticalLayout(existingLoanFieldsContainer, addLoanButton);
        verticalLayout.setMargin(false);
        verticalLayout.setComponentAlignment(addLoanButton, Alignment.BOTTOM_CENTER);
        return verticalLayout;
    }

    @Override
    protected void doSetValue(ExistingLoans value) {
        throw new UnsupportedOperationException();
    }
    
}
