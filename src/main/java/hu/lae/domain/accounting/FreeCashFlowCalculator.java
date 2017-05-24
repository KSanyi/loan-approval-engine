package hu.lae.domain.accounting;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FreeCashFlowCalculator {

    double calculate(IncomeStatementData incomeStatementData, double amortizationRate);

    public static final FreeCashFlowCalculator lastYear = new FreeCashFlowCalculator() {
        
        private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        
        @Override
        public double calculate(IncomeStatementData incomeStatementData, double amortizationRate) {
            IncomeStatement lastIncomeStatement = incomeStatementData.lastIncomeStatement();
            double ebitda = lastIncomeStatement.ebitda;
            double tax = lastIncomeStatement.taxes;
            double amortization = lastIncomeStatement.amortization;
            
            logger.debug("Calculating normalized free cash flow: " + ebitda + " - " + amortization + " * " + amortizationRate + " - " + tax);
            double maintanenceCapex = amortizationRate * amortization;
            return ebitda - maintanenceCapex - tax; 
        }
        
        @Override
        public String toString() {
            return "Last year";
        }
    };
    
    public static final FreeCashFlowCalculator average = new FreeCashFlowCalculator() {
        
        private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        
        @Override
        public double calculate(IncomeStatementData incomeStatementData, double amortizationRate) {
            double averageEbitda = incomeStatementData.incomeStatements().mapToDouble(i -> i.ebitda).average().getAsDouble();
            double averageTax = incomeStatementData.incomeStatements().mapToDouble(i -> i.taxes).average().getAsDouble();
            double lastYearAmortization = incomeStatementData.lastIncomeStatement().amortization;
            
            logger.debug("Calculating normalized free cash flow: " + averageEbitda + " - " + lastYearAmortization + " * " + amortizationRate + " - " + averageTax);
            double maintanenceCapex = amortizationRate * lastYearAmortization;
            return averageEbitda - maintanenceCapex - averageTax; 
        }
        
        @Override
        public String toString() {
            return "Average";
        }
    };
    
    public static List<FreeCashFlowCalculator> calculators() {
        return Arrays.asList(lastYear, average);
    }
    
}

