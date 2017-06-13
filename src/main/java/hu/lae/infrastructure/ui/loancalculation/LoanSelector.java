package hu.lae.infrastructure.ui.loancalculation;

import java.text.DecimalFormat;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.domain.Client;
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.loan.LoanApplicationResult;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.loan.LoanRequest;

@SuppressWarnings("serial")
public class LoanSelector extends CustomField<LoanRequest> {

    private static final DecimalFormat PERCENT_FORMATTER = new DecimalFormat("0.0%");
    
    private int paybackYears;
    
    private final LoanCalculator loanCalculator;
    private final Client client;
    private final FreeCashFlowCalculator freeCashFlowCalculator;
    private final boolean refinanceExistingLongTermLoans;
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");
    
    private final Label debtCapacityUsageLabel = new Label();

    private final double maxDebtCapacity;
    
    public LoanSelector(LoanCalculator loanCalculator, Client client, int paybackYears, FreeCashFlowCalculator freeCashFlowCalculator, LoanRequest loanRequest, boolean refinanceExistingLongTermLoans) {
        this.loanCalculator = loanCalculator;
        this.client = client;
        this.freeCashFlowCalculator = freeCashFlowCalculator;
        this.paybackYears = paybackYears;
        this.refinanceExistingLongTermLoans = refinanceExistingLongTermLoans;
        this.maxDebtCapacity = loanCalculator.calculateIdealLoanRequest(client, freeCashFlowCalculator).sum();
        
        stLoanSlider.addValueChangeListener(v -> shortTermLoanChanged(v.getValue()));
        ltLoanSlider.addValueChangeListener(v -> longTermLoanChanged(v.getValue()));
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYears, 0, freeCashFlowCalculator, refinanceExistingLongTermLoans); 
        stLoanSlider.setMaxLoanValue(loanApplicationResult.maxShortTermLoan);
        ltLoanSlider.setMaxLoanValue(loanApplicationResult.maxLongTermLoan);
        
        stLoanSlider.setValue(loanRequest.shortTermLoan);
        ltLoanSlider.setValue(loanRequest.longTermLoan);
    }
    
    private void shortTermLoanChanged(double shortTermLoan) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYears, shortTermLoan, freeCashFlowCalculator, refinanceExistingLongTermLoans);
        ltLoanSlider.setMaxLoanValue(loanApplicationResult.maxLongTermLoan);
        updateDebtCapacityUsageLabel();
    }
    
    private void longTermLoanChanged(double longTermLoan) {
        updateDebtCapacityUsageLabel();
    }
    
    private void updateDebtCapacityUsageLabel() {
        double debtCapacityUsage = (stLoanSlider.getValue() + ltLoanSlider.getValue() + client.existingLoans.sum()) / maxDebtCapacity;
        debtCapacityUsageLabel.setValue("Debt capacity usage: " + PERCENT_FORMATTER.format(debtCapacityUsage));
    }

    @Override
    public LoanRequest getValue() {
        return new LoanRequest(stLoanSlider.getValue(), ltLoanSlider.getValue());
    }

    @Override
    protected Component initContent() {
        VerticalLayout layout = new VerticalLayout(stLoanSlider, ltLoanSlider, debtCapacityUsageLabel);
        layout.setComponentAlignment(debtCapacityUsageLabel, Alignment.BOTTOM_CENTER);
        layout.setMargin(new MarginInfo(false, true));
        layout.setSpacing(false);
        Panel panel = new Panel(layout);
        panel.setSizeUndefined();
        updateDebtCapacityUsageLabel();
        return panel;
    }

    @Override
    protected void doSetValue(LoanRequest value) {
       throw new IllegalStateException();
    }

}
