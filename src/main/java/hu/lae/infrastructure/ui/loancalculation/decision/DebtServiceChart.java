package hu.lae.infrastructure.ui.loancalculation.decision;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.DefaultScale;

import hu.lae.domain.loan.ExistingLoan;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.loan.LoanRequest;
import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.util.Clock;
import hu.lae.util.ExcelFunctions;
import hu.lae.util.MathUtil;

@SuppressWarnings("serial")
public class DebtServiceChart extends ChartJs {

    DebtServiceChart(double freeCashFlow, ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest, InterestRates interestRates) {
        super(createConfig(freeCashFlow, existingLoansRefinancing, loanRequest, interestRates));
        
        setWidth("1100px");
        setHeight("300px");
    }
    
    private static BarChartConfig createConfig(double freeCashFlow, ExistingLoansRefinancing existingLoansRefinancing, LoanRequest loanRequest, InterestRates interestRates) {
        BarChartConfig config = new BarChartConfig();
        
        List<LocalDate> dates = dates(loanRequest.longTermLoanDuration);
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MMMM");
        List<String> labels = dates.stream().map(date -> date.format(dtf)).collect(Collectors.toList());
        
        config.data()
            .labelsAsList(labels)
            .addDataset(createFreeCFDataset(dates, freeCashFlow));
        
        createExistingLoanDatasets(dates, existingLoansRefinancing, interestRates).stream().forEach(config.data()::addDataset);
        config.data()
            .addDataset(createNewSTLoanDataset(dates, loanRequest.shortTermLoan, interestRates.shortTermInterestRate))
            .addDataset(createNewLTLoanDataset(dates, loanRequest.longTermLoan, loanRequest.longTermLoanDuration, interestRates.longTermInterestRate));
        
        config.options()
            .responsive(true)
            .title()
                .display(true)
                .text("Debt service chart")
                .and()
            .tooltips()
                .mode(InteractionMode.INDEX)
                .intersect(false)
                .and()
            .scales()
            .add(Axis.X, new DefaultScale().stacked(true))
            .add(Axis.Y, new DefaultScale().stacked(true))
            .and()
            .done();
        
        return config;
    }
    
    private static List<LocalDate> dates(int longTermloanDuration) {
        
        return IntStream.range(1, longTermloanDuration * 4)
            .mapToObj(quarter -> Clock.date().plusMonths(quarter * 3))
            .collect(Collectors.toList());
    }
    
    private static BarDataset createFreeCFDataset(List<LocalDate> dates, double freeCashFlow) {
        
        double roundedValue = MathUtil.round(freeCashFlow / 4, 1);
        
        List<Double> data = dates.stream().map(d -> roundedValue).collect(Collectors.toList());
        
        return new BarDataset()
            .label("Free Cash Flow").stack("0").backgroundColor("rgba(151,187,205,0.5)")
            .dataAsList(data);
    }
    
    private static BarDataset createNewSTLoanDataset(List<LocalDate> dates, double shortTermLoan, InterestRate shortTermInterestRate) {
        
    	double yearlyDebtService = shortTermInterestRate.multiply(shortTermLoan);
        double roundedValue = MathUtil.round(yearlyDebtService / 4, 1);
        
        List<Double> data = dates.stream().map(d -> roundedValue).collect(Collectors.toList());
        
        return new BarDataset()
            .label("New Short Term Loan").stack("1").backgroundColor("rgba(0,200,0,0.5)")
            .dataAsList(data);
    }
    
    private static BarDataset createNewLTLoanDataset(List<LocalDate> dates, double longTermLoan, int paybackYears, InterestRate longTermInterestRate) {
        
    	double yearlyDebtService = -ExcelFunctions.pmt(longTermInterestRate.value, paybackYears, longTermLoan);
        double roundedValue = MathUtil.round(yearlyDebtService / 4, 1);
        
        List<Double> data = dates.stream().map(d -> roundedValue).collect(Collectors.toList());
        
        return new BarDataset()
            .label("New Long Term Loan").stack("1").backgroundColor("rgba(0,100,0,0.5)")
            .dataAsList(data);
    }
    
    private static List<BarDataset> createExistingLoanDatasets(List<LocalDate> dates, ExistingLoansRefinancing existingLoansRefinancing, InterestRates interestRates) {
        
        List<BarDataset> datasets = new ArrayList<>();
        int index = 1;
        for(ExistingLoan existingLoan : existingLoansRefinancing.existingLoansRefinancingMap.keySet()) {
        	double yearlyDebtService = existingLoan.calculateYearlyDebtService(interestRates, Clock.date());
            if(existingLoan.isShortTemLoan()) {
                List<Double> data = dates.stream().map(d -> MathUtil.round(yearlyDebtService / 4, 1)).collect(Collectors.toList());
                datasets.add(new BarDataset()
                        .label("Existing Short Term Loan " + index++).stack("1").backgroundColor(existingShortTermLoanColor(index))
                        .dataAsList(data));
            } 
        }
        
        index = 1;
        for(ExistingLoan existingLoan : existingLoansRefinancing.existingLoansRefinancingMap.keySet()) {
            if(existingLoan.isLongTemLoan()) {
            	double yearlyDebtService = existingLoan.calculateYearlyDebtService(interestRates, Clock.date());
                List<Double> data = dates.stream()
                        .map(d -> d.isAfter(existingLoan.expiry.get()) ? 0.0 : MathUtil.round(yearlyDebtService / 4, 1))
                        .collect(Collectors.toList());
                datasets.add(new BarDataset()
                        .label("Existing Long Term Loan " + index++).stack("1").backgroundColor(existingLongTermLoanColor(index))
                        .dataAsList(data));
            }
        }
        
        return datasets;
    }
    
    private static String existingShortTermLoanColor(int index) {
        int rg = 200 + index * 20;
        return "rgba(" + rg + "," + rg + ",0,0.5)";
    }
    
    private static String existingLongTermLoanColor(int index) {
        int rg = 150 + index * 20;
        return "rgba(" + rg + "," + rg + ",0,0.5)";
    }
    
}
