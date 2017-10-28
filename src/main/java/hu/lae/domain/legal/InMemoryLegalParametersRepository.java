package hu.lae.domain.legal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import hu.lae.domain.legal.LegalIssueEvaluation.EvaluationEntry;
import hu.lae.domain.legal.LegalIssueType.Level;

public class InMemoryLegalParametersRepository implements LegalParametersRepository {

    private LegalParameters legalParameters;
    
    public InMemoryLegalParametersRepository() {
        
        List<LegalIssueEvaluation> issueEvaluations = Arrays.asList(
    		new LegalIssueEvaluation(LegalIssueType.BANKRUPTCY, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.LIQUIDATION, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.DISSOLVENCY, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.DELETION, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.FORCED_DELETION, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.EXECUTION, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 100),
	        new LegalIssueEvaluation(LegalIssueType.IDERICT, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.CONFISCATION, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 100),
	        new LegalIssueEvaluation(LegalIssueType.CRIMINAL_CHARGE, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.TAX_NUMBER_SUSPENDED, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.TAX_NUMBER_DELETED, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.ASSET_SETTLEMENT_PROCEDURE, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.DEBT_SETTLEMENT_PROCEDURE, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.TAX_EXECUTION, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 100),
	        new LegalIssueEvaluation(LegalIssueType.BLACK_EMPLOYER, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.ANTICARTEL_PROCEDURE, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10)),
	        new LegalIssueEvaluation(LegalIssueType.CONSUMER_PROTECTION_PROCEDURE, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 100),
	        new LegalIssueEvaluation(LegalIssueType.EMPLOYMENT_FINE, new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 100));
        
        Map<LegalIssueType, LegalIssueEvaluation> map = issueEvaluations.stream().collect(Collectors.toMap(legalIssueEvaluation -> legalIssueEvaluation.legalIssueType, Function.identity()));
        legalParameters = new LegalParameters(2, 1, map);
    }
    
    @Override
    public LegalParameters loadLegalParameters() {
        return legalParameters;
    }

    @Override
    public void updateLegalParameters(LegalParameters legalParameters) {
        this.legalParameters = legalParameters;
    }

}
