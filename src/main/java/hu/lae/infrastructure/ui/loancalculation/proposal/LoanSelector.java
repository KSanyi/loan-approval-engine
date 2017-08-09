package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.text.DecimalFormat;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.domain.Client;
import hu.lae.domain.accounting.FreeCashFlowCalculator;
import hu.lae.domain.loan.CovenantCalculator;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.LoanApplicationResult;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.validation.LiquidityValidator;
import hu.lae.domain.validation.ValidationResult;

@SuppressWarnings("serial")
public class LoanSelector extends CustomField<LoanRequest> {

    private static final DecimalFormat PERCENT_FORMATTER = new DecimalFormat("0.0%");
    
    private int paybackYears;
    
    private final LoanCalculator loanCalculator;
    private final Client client;
    private final FreeCashFlowCalculator freeCashFlowCalculator;
    private final ExistingLoansRefinancing existingLoanRefinancing;
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");
    
    private final Label debtCapacityUsageLabel = new Label();

    private final double maxDebtCapacity;
    
    public LoanSelector(LoanCalculator loanCalculator, Client client, int paybackYears, FreeCashFlowCalculator freeCashFlowCalculator, LoanRequest loanRequest, ExistingLoansRefinancing existingLoanRefinancing) {
        this.loanCalculator = loanCalculator;
        this.client = client;
        this.freeCashFlowCalculator = freeCashFlowCalculator;
        this.paybackYears = paybackYears;
        this.existingLoanRefinancing = existingLoanRefinancing;
        this.maxDebtCapacity = loanCalculator.calculateIdealLoanRequest(client, freeCashFlowCalculator).sum();
        
        stLoanSlider.addValueChangeListener(v -> shortTermLoanChanged(v.getValue()));
        ltLoanSlider.addValueChangeListener(v -> longTermLoanChanged(v.getValue()));
        
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYears, 0, freeCashFlowCalculator, existingLoanRefinancing); 
        stLoanSlider.setMaxLoanValue(loanApplicationResult.maxShortTermLoan);
        ltLoanSlider.setMaxLoanValue(loanApplicationResult.maxLongTermLoan);
        
        stLoanSlider.setValue(loanRequest.shortTermLoan);
        ltLoanSlider.setValue(loanRequest.longTermLoan);
    }
    
    private void shortTermLoanChanged(double shortTermLoan) {
        LoanApplicationResult loanApplicationResult = loanCalculator.calculate(client, paybackYears, shortTermLoan, freeCashFlowCalculator, existingLoanRefinancing);
        ltLoanSlider.setMaxLoanValue(loanApplicationResult.maxLongTermLoan);
        updateDebtCapacityUsageLabel();
        checkLiquidityRatio();
    }
    
    private void checkLiquidityRatio() {
   	 LiquidityValidator liquidityRatioValidator = new LiquidityValidator(client.existingLoans.shortTermLoansSum(), loanCalculator.riskParameters.thresholds.liquidityRatio);
        ValidationResult validationResult = liquidityRatioValidator.validateRatio1(client.financialStatementData(), createLoanRequest());
        if(validationResult.isOk()) {
        	stLoanSlider.setComponentError(null);
        } else {
        	stLoanSlider.setComponentError(new UserError(validationResult.toString()));
        }
   }
    
    private void longTermLoanChanged(double longTermLoan) {
        updateDebtCapacityUsageLabel();
    }
    
    private void updateDebtCapacityUsageLabel() {
        double debtCapacityUsage = CovenantCalculator.calculateDebtgCapacityUsage(createLoanRequest(), maxDebtCapacity, existingLoanRefinancing);
        debtCapacityUsageLabel.setValue("Debt capacity usage: " + PERCENT_FORMATTER.format(debtCapacityUsage));
    }

    private LoanRequest createLoanRequest() {
        return new LoanRequest(stLoanSlider.getValue(), ltLoanSlider.getValue(), paybackYears);
    }
    
    @Override
    public LoanRequest getValue() {
        return createLoanRequest();
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