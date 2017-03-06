package hu.lae.infrastructure.ui.loancalculation;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
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
    
    private final Button checkCalculationButton = new Button("Check calculations");
    
    public CalculatorWindow(MaxLoanDistributor maxLoanDistributor) {
        this.maxLoanDistributor = maxLoanDistributor;
        setModal(true);
        createLayout();
        paybackYearsField.setValue(DEFAULT_PAYBACK_YEARS.toString());
        calculate(DEFAULT_PAYBACK_YEARS, maxLoanDistributor.justifiableShortTermloan);
        stLoanSlider.addLoanValueChangeListener(loanValue -> calculate(Integer.parseInt(paybackYearsField.getValue()), loanValue));
        stLoanSlider.setLoanValue(maxLoanDistributor.justifiableShortTermloan);
        checkCalculationButton.addStyleName(ValoTheme.BUTTON_LINK);
        checkCalculationButton.addClickListener(click -> UI.getCurrent().addWindow(new CalculationsWindow()));
    }
    
    private void createLayout() {
        setWidth("900px");
        setResizable(false);
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        Component component = createPaybackYearsField();
        layout.addComponents(component, stLoanSlider, ltLoanSlider);
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
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
        paybackYearsField.addTextChangeListener(e -> calculate(Integer.parseInt(e.getText()), stLoanSlider.getLoanValue()));
        HorizontalLayout layout = new HorizontalLayout(label, paybackYearsField);
        layout.setSpacing(true);
        return layout;
    }
    
    private void calculate(int paybackYears, long shortTermLoan) {
        stLoanSlider.setMaxLoanValue(maxLoanDistributor.calculateMaxShortTermLoan(paybackYears));
        ltLoanSlider.setMaxLoanValue(maxLoanDistributor.calculateMaxLongTermLoan(paybackYears, shortTermLoan));
    }
    
}
