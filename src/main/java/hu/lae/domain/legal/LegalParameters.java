package hu.lae.domain.legal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import hu.lae.domain.legal.LegalData.LegalIssue;
import hu.lae.domain.legal.LegalIssueType.Level;

public class LegalParameters {

    public final int maxJudgeEntries;
    
    public final int maxLoanMaturityForJudge;
    
    private final Map<LegalIssueType, LegalIssueEvaluation> legalIssueEvaluationMap;

    public LegalParameters(int maxJudgeEntries, int maxLoanMaturityForJudge, List<LegalIssueEvaluation> legalIssueEvaluationList) {
        this.maxJudgeEntries = maxJudgeEntries;
        this.maxLoanMaturityForJudge = maxLoanMaturityForJudge;
        this.legalIssueEvaluationMap = legalIssueEvaluationList.stream().collect(Collectors.toMap(legalIssueEvaluation -> legalIssueEvaluation.legalIssueType, Function.identity()));
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
    
    public List<LegalIssueEvaluation> legalIssuEvaluationEntries() {
    	return new ArrayList<>(legalIssueEvaluationMap.values());
    }
    
    @Override
    public String toString() {
    	return "maxJudgeEntries: " + maxJudgeEntries + " maxLoanMaturityForJudge: " + maxLoanMaturityForJudge + "\n" + 
    			legalIssueEvaluationMap.values().stream().map(l -> l.toString()).collect(Collectors.joining("\n"));	
    }
    
}
