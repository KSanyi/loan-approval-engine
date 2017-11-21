package hu.lae.domain.finance;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class YearlyData<T> {

	public final T tValue;
	
	public final T tMinus1Value;
	
	public final T tMinus2Value;

	public YearlyData(T tValue, T tMinus1Value, T tMinus2Value) {
		this.tValue = tValue;
		this.tMinus1Value = tMinus1Value;
		this.tMinus2Value = tMinus2Value;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
	
}
