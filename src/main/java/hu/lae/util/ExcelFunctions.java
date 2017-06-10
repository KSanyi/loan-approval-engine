package hu.lae.util;

import org.apache.poi.ss.formula.functions.FinanceLib;

public final class ExcelFunctions {

	public static double pmt(double r, double nper, double pv, double fv, int type) {

		double pmt = -r * (pv * Math.pow(1 + r, nper) + fv) / ((1 + r * type) * (Math.pow(1 + r, nper) - 1));
		return pmt;
	}

	/**
	 * Overloaded pmt() call omitting type, which defaults to 0.
	 *
	 * @see #pmt(double, int, double, double, int)
	 */
	public static double pmt(double r, int nper, double pv, double fv) {

		return pmt(r, nper, pv, fv, 0);
	}

	/**
	 * Overloaded pmt() call omitting fv and type, which both default to 0.
	 *
	 * @see #pmt(double, int, double, double, int)
	 */
	public static double pmt(double r, int nper, double pv) {

		return pmt(r, nper, pv, 0);
	}

	/**
	 * Computes the internal rate of return using an estimated irr of 10 percent.
	 *
	 * @param income
	 *            the income values.
	 * @return the irr.
	 */
	public static double irr(Double[] income) {

		return irr(income, 0.1d);
	}

	/**
	 * Calculates IRR using the Newton-Raphson Method.
	 * <p>
	 * Starting with the guess, the method cycles through the calculation until the result is accurate within 0.00001 percent. If IRR can't find a result that works after 20 tries,
	 * the Double.NaN<> is returned.
	 * </p>
	 * <p>
	 * The implementation is inspired by the NewtonSolver from the Apache Commons-Math library,
	 *
	 * @param values
	 *            the income values.
	 * @param guess
	 *            the initial guess of irr.
	 * @return the irr value. The method returns <code>Double.NaN</code> if the maximum iteration count is exceeded
	 * @see <a href="http://commons.apache.org">http://commons.apache.org</a>
	 *      </p>
	 * @see <a href="http://en.wikipedia.org/wiki/Internal_rate_of_return#Numerical_solution"> http://en.wikipedia.org/wiki/Internal_rate_of_return#Numerical_solution</a>
	 * @see <a href="http://en.wikipedia.org/wiki/Newton%27s_method"> http://en.wikipedia.org/wiki/Newton%27s_method</a>
	 */
	public static double irr(Double[] values, double guess) {

		int maxIterationCount = 20;
		double absoluteAccuracy = 1E-7;
		double x0 = guess;
		double x1;
		int i = 0;
		while (i < maxIterationCount) {
			// the value of the function (NPV) and its derivate can be calculated in the same loop
			double fValue = 0;
			double fDerivative = 0;
			for (int k = 0; k < values.length; k++) {
				fValue += values[k] / Math.pow(1.0 + x0, k);
				fDerivative += -k * values[k] / Math.pow(1.0 + x0, k + 1);
			}
			// the essense of the Newton-Raphson Method
			x1 = x0 - fValue / fDerivative;
			if (Math.abs(x1 - x0) <= absoluteAccuracy) {
				return x1;
			}
			x0 = x1;
			++i;
		}
		// maximum number of iterations is exceeded
		return Double.NaN;
	}

	/**
	 * XIRR(values, guess) function, which calculates internal rate of return.
	 * <p>
	 * Starting with the guess, the method cycles through the calculation until the result is accurate within 0.00001 percent. If XIRR can't find a result that works after 4 mill.
	 * tries, the Double.NaN<> is returned.
	 * </p>
	 *
	 * @param values
	 *            - the income values. @param days - the income days.
	 * @param guess
	 *            - the initial guess of irr. @return - the irr value. The method returns <code>Double.NaN</code> if the maximum iteration count is exceeded
	 */
	public static double xirr(double[] values, double[] days, double guess) {

		int maxIterationCount = 40;
		double absoluteAccuracy = 1E-7;

		double x0 = guess;
		double x1;

		int i = 0;
		while (i < maxIterationCount) {

			// the value of the function (NPV) and its derivate can be calculated in the same loop
			double fValue = 0;
			double fDerivative = 0;
			for (int k = 0; k < values.length; k++) {
				fValue += values[k] / Math.pow(1.0 + x0, (int) days[k] / 365.0);
				fDerivative += -((int) days[k] / 365.0) * values[k] / Math.pow(1.0 + x0, (int) days[k] / 365.0 + 1);
				// fValue += values[k] / Math.pow(1.0 + x0, days[k] / 365.0);
				// fDerivative += -(days[k] / 365.0) * values[k] / Math.pow(1.0 + x0, (days[k] / 365.0) + 1);
			}

			// the essense of the Newton-Raphson Method
			x1 = x0 - fValue / fDerivative;

			if (Math.abs(x1 - x0) <= absoluteAccuracy) {
				return x1;
			}

			x0 = x1;
			++i;
		}
		// maximum number of iterations is exceeded
		return Double.NaN;
	}

	/**
	 * Emulates Excel/Calc's IPMT(interest_rate, period, number_payments, PV, FV, Type) function, which calculates the portion of the payment at a given period that is the interest
	 * on previous balance.
	 *
	 * @param r
	 *            - periodic interest rate represented as a decimal.
	 * @param per
	 *            - period (payment number) to check value at.
	 * @param nper
	 *            - number of total payments / periods.
	 * @param pv
	 *            - present value -- borrowed or invested principal.
	 * @param fv
	 *            - future value of loan or annuity.
	 * @param type
	 *            - when payment is made: beginning of period is 1; end, 0.
	 * @return <code>double</code> representing interest portion of payment.
	 * @see #pmt(double, int, double, double, int)
	 * @see #fv(double, int, double, double, int)
	 */
	// http://doc.optadata.com/en/dokumentation/application/expression/functions/financial.html
	public static double ipmt(double r, int per, int nper, double pv, double fv, int type) {

		double ipmt = fv(r, per - 1, pmt(r, nper, pv, fv, type), pv, type) * r;
		if (type == 1) {
			ipmt /= 1 + r;
		}
		return ipmt;
	}

	public static double ipmt(double r, int per, int nper, double pv, double fv) {

		return ipmt(r, per, nper, pv, fv, 0);
	}

	public static double ipmt(double r, int per, int nper, double pv) {

		return ipmt(r, per, nper, pv, 0);
	}

	/**
	 * Emulates Excel/Calc's PPMT(interest_rate, period, number_payments, PV, FV, Type) function, which calculates the portion of the payment at a given period that will apply to
	 * principal.
	 *
	 * @param r
	 *            - periodic interest rate represented as a decimal.
	 * @param per
	 *            - period (payment number) to check value at.
	 * @param nper
	 *            - number of total payments / periods.
	 * @param pv
	 *            - present value -- borrowed or invested principal.
	 * @param fv
	 *            - future value of loan or annuity.
	 * @param type
	 *            - when payment is made: beginning of period is 1; end, 0.
	 * @return <code>double</code> representing principal portion of payment.
	 * @see #pmt(double, int, double, double, int)
	 * @see #ipmt(double, int, int, double, double, int)
	 */
	public static double ppmt(double r, int per, int nper, double pv, double fv, int type) {

		return pmt(r, nper, pv, fv, type) - ipmt(r, per, nper, pv, fv, type);
	}

	public static double ppmt(double r, int per, int nper, double pv, double fv) {

		return pmt(r, nper, pv, fv) - ipmt(r, per, nper, pv, fv);
	}

	public static double ppmt(double r, int per, int nper, double pv) {

		return pmt(r, nper, pv) - ipmt(r, per, nper, pv);
	}

	/**
	 * Emulates Excel/Calc's FV(interest_rate, number_payments, payment, PV, Type) function, which calculates future value or principal at period N.
	 *
	 * @param r
	 *            - periodic interest rate represented as a decimal.
	 * @param nper
	 *            - number of total payments / periods.
	 * @param pmt
	 *            - periodic payment amount.
	 * @param pv
	 *            - present value -- borrowed or invested principal.
	 * @param type
	 *            - when payment is made: beginning of period is 1; end, 0.
	 * @return <code>double</code> representing future principal value.
	 */
	// http://en.wikipedia.org/wiki/Future_value
	public static double fv(double r, int nper, double pmt, double pv, int type) {

		double fv = -(pv * Math.pow(1 + r, nper) + pmt * (1 + r * type) * (Math.pow(1 + r, nper) - 1) / r);
		return fv;
	}
	
	public static double nper(double r, double y, double p, double f, boolean t) {
	    return FinanceLib.nper(r, y, p, f, t);
	}
}
