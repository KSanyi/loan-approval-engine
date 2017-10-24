package hu.lae.domain.legal;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LegalData {

	public static LegalData empty = new LegalData(Collections.emptyList());
	
    public final List<LegalIssue> legalIssues;
    
    public LegalData(List<LegalIssue> legalIssues) {
        this.legalIssues = Collections.unmodifiableList(legalIssues);
    }

    public static class LegalIssue {
        
        public final LegalIssueType type;
        
        public final Optional<LocalDate> date;
        
        public final Entity entity;

        public LegalIssue(LegalIssueType type, Optional<LocalDate> date, Entity entity) {
            this.type = type;
            this.date = date;
            this.entity = entity;
        }
        
        public boolean isInProgress() {
            return !date.isPresent();
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
        
    }
    
    public static enum Entity {
        COMPANY("Company"), COMPANY_GROUP("Company Group");
    	
    	public final String displayName;

		private Entity(String displayName) {
			this.displayName = displayName;
		}
    	 
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
