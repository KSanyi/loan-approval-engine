package hu.lae.domain.loan;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LoanApplicationResult {

    public final double justifiableShortTermLoan;
    
    public final double maxShortTermLoan;
    
    public final double maxLongTermLoan;
    
    public LoanApplicationResult(double justifiableShortTermLoan, double maxShortTermLoan, double maxLongTermLoan) {
        this.justifiableShortTermLoan = justifiableShortTermLoan;
        this.maxLongTermLoan = maxLongTermLoan;
        this.maxShortTermLoan = maxShortTermLoan;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
