package hu.lae.accounting;

import java.lang.invoke.MethodHandles;

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
    };
    
}

