package hu.lae.infrastructure.ui.loancalculation;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.Client;
import hu.lae.accounting.FreeCashFlowCalculator;
import hu.lae.loan.LoanApplicationResult;
import hu.lae.loan.LoanCalculator;
import hu.lae.loan.LoanRequest;

@SuppressWarnings("serial")
public class LoanSelector extends CustomField<LoanRequest> {

    private int paybackYears;
    
    private final LoanCalculator loanCalculator;
    private final Client client;
    private final FreeCashFlowCalculator freeCashFlowCalculator;
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");

    public LoanSelector(LoanCalculator loanCalculator, Client client, int paybackYears, FreeCashFlowCalculator freeCashFlowCalculator) {
        this.loanCalculator = loanCalculator;
        this.client = client;
        this.freeCashFlowCalculator = freeCashFlowCalculator;
        this.paybackYears = paybackYears;
        stLoanSlider.addValueChangeListener(v -> shortTermloanChanged(v.getValue()));
    }
    
    private void shortTermloanChanged(double shortTermLoan) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYears, shortTermLoan, freeCashFlowCalculator);
        ltLoanSlider.setMaxLoanValue(loanApplicationResult.maxLongTermLoan);
    }

    @Override
    public LoanRequest getValue() {
        return new LoanRequest(stLoanSlider.getValue(), ltLoanSlider.getValue());
    }

    @Override
    protected Component initContent() {
        VerticalLayout layout = new VerticalLayout(stLoanSlider, ltLoanSlider);
        layout.setMargin(new MarginInfo(false, true));
        layout.setSpacing(false);
        Panel panel = new Panel(layout);
        panel.setSizeUndefined();
        return panel;
    }

    @Override
    protected void doSetValue(LoanRequest value) {
       throw new IllegalStateException();
    }
    
    public void setPaybackYears(int paybackYears) {
        this.paybackYears = paybackYears;
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYears, stLoanSlider.getValue(), freeCashFlowCalculator);
        stLoanSlider.setMaxLoanValue(loanApplicationResult.maxShortTermLoan);
        stLoanSlider.setValue(loanApplicationResult.justifiableShortTermLoan);
        stLoanSlider.setDescription("Justifiable short term loan:<br/><center>" + loanApplicationResult.justifiableShortTermLoan + " million Ft</center>", ContentMode.HTML);
    }

}
