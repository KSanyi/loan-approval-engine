package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.ComboBox;
import hu.lae.infrastructure.ui.component.DateField;
import hu.lae.util.Clock;

@SuppressWarnings("serial")
class MaturityField extends CustomField<LocalDate>{

    private final ComboBox<Integer> yearsCombo;
    
    private final DateField dateField;
    
    MaturityField(int maxLoanDuration) {
        setCaption("Select maturity date or years");
        yearsCombo = new ComboBox<>(null, generateComboValues(maxLoanDuration));
        yearsCombo.setItemCaptionGenerator(year -> year + " year" + (year > 1 ? "s" : ""));
        yearsCombo.setValue(maxLoanDuration);
        yearsCombo.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        yearsCombo.setWidth("105px");
        yearsCombo.setEmptySelectionAllowed(false);
        
        dateField = new DateField(null, "loan.request.maturity.date");
        dateField.setRangeStart(Clock.date().plusYears(1));
        dateField.setRangeEnd(Clock.date().plusYears(maxLoanDuration));
        dateField.setValue(Clock.date().plusYears(maxLoanDuration));
        dateField.setWidth("130px");
        
        yearsCombo.addValueChangeListener(e -> dateField.setValue(Clock.date().plusYears(e.getValue())));
        dateField.addValueChangeListener(e -> yearsCombo.setValue((int)ChronoUnit.YEARS.between(Clock.date(), e.getValue())));
    }
    
    @Override
    public LocalDate getValue() {
        return dateField.getValue();
    }

    @Override
    protected Component initContent() {
        return new HorizontalLayout(dateField, yearsCombo);
    }

    @Override
    protected void doSetValue(LocalDate value) {
        dateField.setValue(value);
    }
    
    private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }

}
