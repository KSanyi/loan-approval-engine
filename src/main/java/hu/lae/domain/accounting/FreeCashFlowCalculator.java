package hu.lae.domain.accounting;

import java.util.Arrays;
import java.util.List;

public interface FreeCashFlowCalculator {

    double calculate(IncomeStatementHistory incomeStatementData, double amortizationRate);

    public static final FreeCashFlowCalculator lastYear = new FreeCashFlowCalculator() {
        
        @Override
        public double calculate(IncomeStatementHistory incomeStatementData, double amortizationRate) {
            IncomeStatement lastIncomeStatement = incomeStatementData.lastIncomeStatement();
            double ebitda = lastIncomeStatement.ebitda;
            double tax = lastIncomeStatement.taxes;
            double amortization = lastIncomeStatement.amortization;
            
            double maintanenceCapex = amortizationRate * amortization;
            return ebitda - maintanenceCapex - tax; 
        }
        
        @Override
        public String toString() {
            return "Last year";
        }
    };
    
    public static final FreeCashFlowCalculator average = new FreeCashFlowCalculator() {
        
        @Override
        public double calculate(IncomeStatementHistory incomeStatementData, double amortizationRate) {
            double averageEbitda = incomeStatementData.incomeStatements().mapToDouble(i -> i.ebitda).average().getAsDouble();
            double averageTax = incomeStatementData.incomeStatements().mapToDouble(i -> i.taxes).average().getAsDouble();
            double lastYearAmortization = incomeStatementData.lastIncomeStatement().amortization;
            
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

