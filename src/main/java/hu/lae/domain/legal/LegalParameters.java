package hu.lae.domain.legal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.domain.legal.LegalIssueType.Level;

public class LegalParameters {

    public final int maxJudgeEntries;
    
    public final int maxLoanMaturityForJudge;
    
    public final Map<LegalIssueType, LegalIssueEvaluation> legalIssueEvaluationMap;

    public LegalParameters(int maxJudgeEntries, int maxLoanMaturityForJudge, Map<LegalIssueType, LegalIssueEvaluation> legalIssueEvaluationMap) {
        this.maxJudgeEntries = maxJudgeEntries;
        this.maxLoanMaturityForJudge = maxLoanMaturityForJudge;
        this.legalIssueEvaluationMap = legalIssueEvaluationMap;
    }
    
    public LegalEvaluationResult evaluate(LegalData legalData) {
        
        Map<Level, List<LegalIssue>> issuesByLevel = legalData.legalIssues.stream()
                .collect(Collectors.groupingBy(issue -> legalIssueEvaluationMap.get(issue.type).evaluate(issue)));
        
        List<LegalIssue> noGoIssues = issuesByLevel.getOrDefault(Level.NOGO, Collections.emptyList());
        if(!noGoIssues.isEmpty()) {
            return LegalEvaluationResult.noGo(noGoIssues);
        } else {
            List<LegalIssue> judgeIssues = issuesByLevel.getOrDefault(Level.JUDGE, Collections.emptyList());
            if(!judgeIssues.isEmpty()) {
                if(judgeIssues.size() > maxJudgeEntries) {
                    return LegalEvaluationResult.noGo(judgeIssues);
                } else {
                    return LegalEvaluationResult.judge(maxLoanMaturityForJudge, judgeIssues);
                }
            }
        }
        
        return LegalEvaluationResult.go();
    }
    
    @Override
    public String toString() {
    	return "maxJudgeEntries: " + maxJudgeEntries + " maxLoanMaturityForJudge: " + maxLoanMaturityForJudge + "\n" + 
    			legalIssueEvaluationMap.values().stream().map(l -> l.toString()).collect(Collectors.joining("\n"));	
    }
    
}
