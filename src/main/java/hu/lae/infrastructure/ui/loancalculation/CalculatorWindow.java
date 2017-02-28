package hu.lae.infrastructure.ui.loancalculation;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.loan.MaxLoanDistributor;

@SuppressWarnings("serial")
public class CalculatorWindow extends Window {

    private static final Integer DEFAULT_PAYBACK_YEARS = 5;
    
    private final MaxLoanDistributor maxLoanDistributor;
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");

    private final TextField paybackYearsField = new TextField();
    
    public CalculatorWindow(MaxLoanDistributor maxLoanDistributor) {
        this.maxLoanDistributor = maxLoanDistributor;
        setModal(true);
        createLayout();
        paybackYearsField.setValue(DEFAULT_PAYBACK_YEARS.toString());
        calculate(DEFAULT_PAYBACK_YEARS);
        stLoanSlider.addLoanValueChangeListener(e -> calculate(Integer.parseInt(paybackYearsField.getValue())));
        stLoanSlider.setMaxLoanValue(maxLoanDistributor.maxShortTermloan);
        stLoanSlider.setLoanValue(maxLoanDistributor.maxShortTermloan / 2);
    }
    
    private void createLayout() {
        setWidth("900px");
        setResizable(false);
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponents(createPaybackYearsField(), stLoanSlider, ltLoanSlider);
        Panel panel = new Panel("Calculation", layout);
        panel.addStyleName("colored");
        setContent(panel);
    }
    
    private HorizontalLayout createPaybackYearsField() {
        Label label = new Label("Payback years");
        label.addStyleName(ValoTheme.LABEL_H2);
        paybackYearsField.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        paybackYearsField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        paybackYearsField.setWidth("40px");
        paybackYearsField.setMaxLength(2);
        paybackYearsField.addTextChangeListener(e -> calculate(Integer.parseInt(e.getText())));
        HorizontalLayout layout = new HorizontalLayout(label, paybackYearsField);
        layout.setSpacing(true);
        return layout;
    }
    
    private void calculate(int paybackYears) {
        long shortTermLoan = stLoanSlider.getLoanValue();
        ltLoanSlider.setMaxLoanValue(maxLoanDistributor.maxLongTermLoan(paybackYears, shortTermLoan));
    }
    
}
