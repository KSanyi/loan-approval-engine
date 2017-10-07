package hu.lae.domain.loan;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.util.MapFactory;

public class DSCRCalculatorTest {

	private final LocalDate date = LocalDate.of(2017,4,1);
	
	private final InterestRates interestRates = new InterestRates(0.03, 0.05);
	
	@Test
	public void noExistingLoan1() {
		
		double freeCashFlow = 300;
		LoanRequest loanRequest = new LoanRequest(400, 1000, 5);
		ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(ExistingLoans.createEmpty(), false);
		
		double dscr = DSCRCalculator.calculateDSCR(interestRates, freeCashFlow, loanRequest, existingLoansRefinancing, date);
        
        Assert.assertEquals(1.23, dscr, 0.01);
	}
	
	@Test
	public void noExistingLoan2() {
		
		double freeCashFlow = 100;
		LoanRequest loanRequest = new LoanRequest(200, 100, 3);
		ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(ExistingLoans.createEmpty(), false);
		
		double dscr = DSCRCalculator.calculateDSCR(interestRates, freeCashFlow, loanRequest, existingLoansRefinancing, date);
        
        Assert.assertEquals(2.34, dscr, 0.01);
	}
	
	@Test
	public void existingRefinanciableLoans() {
		
		double freeCashFlow = 300;
		LoanRequest loanRequest = new LoanRequest(400, 1000, 5);
		ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(MapFactory.of(
				ExistingLoan.newShortTermLoan(400, false), true,
				ExistingLoan.newLongTermLoan(1000, LocalDate.of(2022, 4, 1), false), true));
		  
		double dscr = DSCRCalculator.calculateDSCR(interestRates, freeCashFlow, loanRequest, existingLoansRefinancing, date);
        
        Assert.assertEquals(1.23, dscr, 0.01);
	}
	
	@Test
	public void existingNonRefinanciableLoans() {
		
		double freeCashFlow = 300;
		LoanRequest loanRequest = new LoanRequest(400, 1000, 5);
		ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(MapFactory.of(
				ExistingLoan.newShortTermLoan(400, false), false,
				ExistingLoan.newLongTermLoan(1000, LocalDate.of(2022, 4, 1), false), false));
		
		double dscr = DSCRCalculator.calculateDSCR(interestRates, freeCashFlow, loanRequest, existingLoansRefinancing, date);
        
        Assert.assertEquals(0.52, dscr, 0.01);
	}
	
	@Test
	public void tamasExample() {
		
		double freeCashFlow = 240;
		LoanRequest loanRequest = new LoanRequest(200, 100, 5);
		ExistingLoansRefinancing existingLoansRefinancing = new ExistingLoansRefinancing(MapFactory.of(
				ExistingLoan.newShortTermLoan(100, false), true,
				ExistingLoan.newLongTermLoan(150, LocalDate.of(2021,2,12), false), false,
				ExistingLoan.newLongTermLoan(200, LocalDate.of(2020,4, 1), false), false));
		
		double dscr = DSCRCalculator.calculateDSCR(interestRates, freeCashFlow, loanRequest, existingLoansRefinancing, date);
        
        Assert.assertEquals(1.38, dscr, 0.01);
	}
	
}
