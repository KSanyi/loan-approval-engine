package hu.lae.domain.legal;

import java.util.HashMap;
import java.util.Map;

import hu.lae.domain.legal.LegalIssueEvaluation.EvaluationEntry;
import hu.lae.domain.legal.LegalIssueType.Level;

public class InMemoryLegalParametersRepository implements LegalParametersRepository {

    private LegalParameters legalParameters;
    
    public InMemoryLegalParametersRepository() {
        Map<LegalIssueType, LegalIssueEvaluation> map = new HashMap<>();
        map.put(LegalIssueType.BANKRUPTCY, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.LIQUIDATION, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.DISSOLVENCY, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.DELETION, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.FORCED_DELETION, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.EXECUTION, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.IDERICT, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.CONFISCATION, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.CRIMINAL_CHARGE, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.TAX_NUMBER_SUSPENDED, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.TAX_NUMBER_DELETED, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.ASSET_SETTLEMENT_PROCEDURE, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.DEBT_SETTLEMENT_PROCEDURE, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.TAX_EXECUTION, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.BLACK_EMPLOYER, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.ANTICARTEL_PROCEDURE, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.CONSUMER_PROTECTION_PROCEDURE, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
        map.put(LegalIssueType.EMPLOYMENT_FINE, new LegalIssueEvaluation(new EvaluationEntry(Level.JUDGE, Level.GO, 10), new EvaluationEntry(Level.JUDGE, Level.GO, 10), 0));
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
