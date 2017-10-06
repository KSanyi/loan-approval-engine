package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.lae.infrastructure.ui.component.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalParameters;

@SuppressWarnings("serial")
public class LegalParametersForm extends CustomField<LegalParameters> {

    private final ComboBox<Integer> maxJudgeEntriesCombo = new ComboBox<>("Max judge entries", generateComboValues(10));
    private final ComboBox<Integer> maxLoanMaturityForJudgeCombo = new ComboBox<>("Max loan maturity for judge", generateComboValues(5));
    
    private final LegalParametersTable legalParametersTable;
    
    public LegalParametersForm(LegalParameters legalParameters) {
        
        maxJudgeEntriesCombo.setValue(legalParameters.maxJudgeEntries);
        maxLoanMaturityForJudgeCombo.setValue(legalParameters.maxLoanMaturityForJudge);
        
        legalParametersTable = new LegalParametersTable(legalParameters.legalIssueEvaluationMap);
    }
    
    @Override
    public LegalParameters getValue() {
        return new LegalParameters(maxJudgeEntriesCombo.getValue(), maxLoanMaturityForJudgeCombo.getValue(), legalParametersTable.getLegalIssueEvaluationMap());
    }

    @Override
    protected Component initContent() {
        
        maxJudgeEntriesCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        maxJudgeEntriesCombo.setWidth("70px");
        maxJudgeEntriesCombo.setEmptySelectionAllowed(false);
        
        maxLoanMaturityForJudgeCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        maxLoanMaturityForJudgeCombo.setWidth("70px");
        maxLoanMaturityForJudgeCombo.setEmptySelectionAllowed(false);
        
        VerticalLayout layout = new VerticalLayout(new HorizontalLayout(maxJudgeEntriesCombo, maxLoanMaturityForJudgeCombo), legalParametersTable);
        layout.setMargin(false);
        return layout;
    }

    @Override
    protected void doSetValue(LegalParameters legalParameters) {
        throw new IllegalStateException();
    }
    
    private static List<Integer> generateComboValues(int maxValue) {
        return IntStream.range(1, maxValue + 1).mapToObj(i -> i).collect(Collectors.toList());
    }

}
