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
import hu.lae.domain.finance.FreeCashFlowCalculator;
import hu.lae.domain.loan.CovenantCalculator;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.LoanCalculator;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.validation.LiquidityValidator;
import hu.lae.domain.validation.ValidationResult;
import hu.lae.util.Clock;

@SuppressWarnings("serial")
public class LoanSelector extends CustomField<LoanRequest> {

    private static final DecimalFormat PERCENT_FORMATTER = new DecimalFormat("0.0%");
    
    private final LoanCalculator loanCalculator;
    private final Client client;
    private final FreeCashFlowCalculator freeCashFlowCalculator;
    private final ExistingLoansRefinancing existingLoansRefinancing;
    private final LoanRequest loanRequest;
    
    private final LoanSlider stLoanSlider = new LoanSlider("Short term loan");
    private final LoanSlider ltLoanSlider = new LoanSlider("Long term loan");
    
    private final Label debtCapacityUsageLabel = new Label();

    private final double maxDebtCapacity;
    
    public LoanSelector(LoanCalculator loanCalculator, Client client, FreeCashFlowCalculator freeCashFlowCalculator, LoanRequest loanRequest, ExistingLoansRefinancing existingLoansRefinancing) {
        this.loanCalculator = loanCalculator;
        this.client = client;
        this.freeCashFlowCalculator = freeCashFlowCalculator;
        this.existingLoansRefinancing = existingLoansRefinancing;
        this.maxDebtCapacity = loanCalculator.calculateIdealLoanRequest(client, freeCashFlowCalculator).sum();
        this.loanRequest = loanRequest;
        
        stLoanSlider.setValue(loanRequest.shortTermLoan);
        ltLoanSlider.setValue(loanRequest.longTermLoan);
        
        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, loanRequest.longTermLoan, loanRequest.maturityYears(), existingLoansRefinancing, freeCashFlowCalculator);
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, loanRequest.shortTermLoan, loanRequest.maturityYears(), existingLoansRefinancing, freeCashFlowCalculator);
        stLoanSlider.setMaxLoanValue(maxShortTermLoan);
        ltLoanSlider.setMaxLoanValue(maxLongTermLoan);
        
        stLoanSlider.addValueChangeListener(v -> shortTermLoanChanged(v.getValue()));
        ltLoanSlider.addValueChangeListener(v -> longTermLoanChanged(v.getValue()));
    }
    
    private void shortTermLoanChanged(double shortTermLoan) {
        double maxLongTermLoan = loanCalculator.calculateMaxLongTermLoan(client, shortTermLoan, loanRequest.maturityYears(), existingLoansRefinancing, freeCashFlowCalculator);
        ltLoanSlider.setMaxLoanValue(maxLongTermLoan);
        updateDebtCapacityUsageLabel();
        checkLiquidityRatio();
    }
    
    private void checkLiquidityRatio() {
   	 LiquidityValidator liquidityRatioValidator = new LiquidityValidator(existingLoansRefinancing.nonRefinancableShortTermLoans(), loanCalculator.riskParameters.thresholds.liquidityRatio);
        ValidationResult validationResult = liquidityRatioValidator.validateRatio1(client.financialStatementData(), createLoanRequest());
        if(validationResult.isOk()) {
        	stLoanSlider.setComponentError(null);
        } else {
        	stLoanSlider.setComponentError(new UserError(validationResult.toString()));
        }
   }
    
    private void longTermLoanChanged(double longTermLoan) {
        double maxShortTermLoan = loanCalculator.calculateMaxShortTermLoan(client, longTermLoan, loanRequest.maturityYears(), existingLoansRefinancing, freeCashFlowCalculator);
        stLoanSlider.setMaxLoanValue(maxShortTermLoan);
        updateDebtCapacityUsageLabel();
    }
    
    private void updateDebtCapacityUsageLabel() {
        double debtCapacityUsage = CovenantCalculator.calculateDebtCapacityUsage(createLoanRequest(), maxDebtCapacity, existingLoansRefinancing);
        debtCapacityUsageLabel.setValue("Debt capacity usage: " + PERCENT_FORMATTER.format(debtCapacityUsage));
    }

    private LoanRequest createLoanRequest() {
        
        return new LoanRequest(stLoanSlider.getValue(), ltLoanSlider.getValue(), Clock.date(), loanRequest.longTermLoanMaturityDate);
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
