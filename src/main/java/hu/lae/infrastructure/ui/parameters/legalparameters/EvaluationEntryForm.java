package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalIssueEvaluation.EvaluationEntry;
import hu.lae.domain.legal.LegalIssueType.Level;

@SuppressWarnings("serial")
public class EvaluationEntryForm extends CustomField<EvaluationEntry> {

    private final ComboBox<Level> inProgressLevelCombo = new ComboBox<>(null, Arrays.asList(Level.values()));
    private final ComboBox<Level> inHistoryLevelCombo = new ComboBox<>(null, Arrays.asList(Level.values()));
    private final ComboBox<Integer> limitationYearsCombo = new ComboBox<>(null, generateComboValues());
    
    EvaluationEntryForm(String caption, EvaluationEntry evaluationEntry) {
        setCaption(caption);
        inProgressLevelCombo.setValue(evaluationEntry.inProgressLevel);
        inHistoryLevelCombo.setValue(evaluationEntry.inHistoryLevel);
        limitationYearsCombo.setValue(evaluationEntry.limitationYears);
    }
    
    @Override
    public EvaluationEntry getValue() {
        return new EvaluationEntry(inProgressLevelCombo.getValue(), inHistoryLevelCombo.getValue(), limitationYearsCombo.getValue());
    }

    @Override
    protected Component initContent() {
        inProgressLevelCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        inProgressLevelCombo.setWidth("90px");
        inProgressLevelCombo.setEmptySelectionAllowed(false);
        
        inHistoryLevelCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        inHistoryLevelCombo.setWidth("90px");
        inHistoryLevelCombo.setEmptySelectionAllowed(false);
        
        limitationYearsCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        limitationYearsCombo.setWidth("60px");
        limitationYearsCombo.setEmptySelectionAllowed(false);
        
        HorizontalLayout layout = new HorizontalLayout(inProgressLevelCombo, inHistoryLevelCombo, limitationYearsCombo);
        return layout;
    }

    @Override
    protected void doSetValue(EvaluationEntry value) {
       throw new IllegalStateException();
        
    }
    
    private static List<Integer> generateComboValues() {
        return IntStream.range(1, 101).mapToObj(i -> i).collect(Collectors.toList());
    }

}
