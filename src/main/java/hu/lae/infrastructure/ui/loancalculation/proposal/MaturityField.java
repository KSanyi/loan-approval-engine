package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.infrastructure.ui.component.DateField;
import hu.lae.util.Clock;

@SuppressWarnings("serial")
class MaturityField extends CustomField<LocalDate> {

    private final DateField dateField;
    
    private final List<YearButton> yearButtons;
    
    MaturityField(int maxLoanDuration) {
        setCaption("Select maturity date");
        
        dateField = new DateField(null, "loan.request.maturity.date");
        dateField.setRangeStart(Clock.date().plusYears(1));
        dateField.setRangeEnd(Clock.date().plusYears(maxLoanDuration));
        dateField.setValue(Clock.date().plusYears(maxLoanDuration));
        dateField.setWidth("140px");
        
        yearButtons = IntStream.range(1, 6).mapToObj(year -> new YearButton(year, dateField)).collect(Collectors.toList());
        yearButtons.stream().forEach(button -> button.setEnabled(button.years <= maxLoanDuration));
    }
    
    @Override
    public LocalDate getValue() {
        return dateField.getValue();
    }

    @Override
    protected Component initContent() {
    	HorizontalLayout yearButtonsBar = new HorizontalLayout(yearButtons.toArray(new YearButton[0]));
        return new HorizontalLayout(dateField, yearButtonsBar);
    }

    @Override
    protected void doSetValue(LocalDate value) {
        dateField.setValue(value);
    }
    
    static class YearButton extends Button {
    	
    	final int years;
    	
		public YearButton(int years, DateField dateField) {
			this.years = years;
			setCaption(years + " year" + (years > 1 ? "s" : ""));
			addStyleName(ValoTheme.BUTTON_TINY);
			
			setWidth("60px");
			setHeight("20px");
			
			addClickListener(click -> dateField.setValue(Clock.date().plusYears(years)));
		}
    	
    }

}
