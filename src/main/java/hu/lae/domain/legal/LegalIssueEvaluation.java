package hu.lae.domain.legal;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.lae.domain.legal.LegalData.Entity;
import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.domain.legal.LegalIssueType.Level;
import hu.lae.util.Clock;

public class LegalIssueEvaluation {

    public final EvaluationEntry companyEntry;
    
    public final EvaluationEntry companyGroupEntry;
    
    public final Optional<Integer> materialityThreshold;
    
    private LegalIssueEvaluation(EvaluationEntry companyEntry, EvaluationEntry companyGroupEntry, Optional<Integer> materialityThreshold) {
        this.companyEntry = companyEntry;
        this.companyGroupEntry = companyGroupEntry;
        this.materialityThreshold = materialityThreshold;
    }
    
    public LegalIssueEvaluation(EvaluationEntry companyEntry, EvaluationEntry companyGroupEntry) {
        this(companyEntry, companyGroupEntry, Optional.empty());
    }
    
    public LegalIssueEvaluation(EvaluationEntry companyEntry, EvaluationEntry companyGroupEntry, int materialityThreshold) {
        this(companyEntry, companyGroupEntry, Optional.of(materialityThreshold));
    }
    
    public Level evaluate(LegalIssue issue) {
        if(issue.entity == Entity.COMPANY) {
            return companyEntry.evaluate(issue);
        } else {
            return companyGroupEntry.evaluate(issue);
        }
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static class EvaluationEntry {
        
        public final Level inProgressLevel;
        
        public final Level inHistoryLevel;
        
        public final Optional<Integer> limitationYears;

        public EvaluationEntry(Level inProgressLevel, Level inHistoryLevel) {
            this(inProgressLevel, inHistoryLevel, Optional.empty());
        }
        
        public EvaluationEntry(Level inProgressLevel, Level inHistoryLevel, int limitationYears) {
            this(inProgressLevel, inHistoryLevel, Optional.of(limitationYears));
        }
        
        public EvaluationEntry(Level inProgressLevel, Level inHistoryLevel, Optional<Integer> limitationYears) {
            this.inProgressLevel = inProgressLevel;
            this.inHistoryLevel = inHistoryLevel;
            this.limitationYears = limitationYears;
        }
        
        public Level evaluate(LegalIssue issue) {
            if(issue.isInProgress()) {
                return inProgressLevel;
            } else {
            	int usedLimitationYears = limitationYears.orElse(Integer.MAX_VALUE);
                if(issue.date.get().isAfter(Clock.date().minusYears(usedLimitationYears))) {
                    return inHistoryLevel;
                } else {
                    return Level.GO;
                }
            }
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
