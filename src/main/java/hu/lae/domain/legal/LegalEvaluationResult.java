package hu.lae.domain.legal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.legal.LegalData.LegalIssue;

public class LegalEvaluationResult {

    public final Value value;
    
    public final Optional<Integer> maxLoanDuration;
    
    public final List<LegalIssue> issues;
    
    private LegalEvaluationResult(Value value, Optional<Integer> maxLoanDuration, List<LegalIssue> issues) {
        this.maxLoanDuration = maxLoanDuration;
        this.value = value;
        this.issues = issues;
    }

    public static enum Value {
        GO, NOGO, JUDGE
    }
    
    public static LegalEvaluationResult go() {
        return new LegalEvaluationResult(Value.GO, Optional.empty(), Collections.emptyList());
    }
    
    public static LegalEvaluationResult noGo(List<LegalIssue> issues) {
        return new LegalEvaluationResult(Value.NOGO, Optional.empty(), issues);
    }
    
    public static LegalEvaluationResult judge(int loanYears, List<LegalIssue> issues) {
        return new LegalEvaluationResult(Value.JUDGE, Optional.of(loanYears), issues);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
