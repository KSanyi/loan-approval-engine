package hu.lae.domain.finance.ebitdacorrection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EbitdaCorrectionInput {

	public final long sales;
    
    public final long ebitda;
    
    public final double buyersDays;

    public EbitdaCorrectionInput(long sales, long ebitda, double buyersDays) {
        this.sales = sales;
        this.ebitda = ebitda;
        this.buyersDays = buyersDays;
    }
    
    public double ebitdaMargin() {
        
        return sales != 0 ? (double)ebitda / sales : 0;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
	
}
