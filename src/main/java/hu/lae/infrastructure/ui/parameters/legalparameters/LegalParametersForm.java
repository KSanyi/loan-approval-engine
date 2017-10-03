package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalIssueEvaluation.EvaluationEntry;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.domain.legal.LegalParameters;
import hu.lae.infrastructure.ui.VaadinUtil;

@SuppressWarnings("serial")
public class LegalParametersForm extends CustomField<LegalParameters> {

    private final Grid<Row> grid;
    
    private LegalParameters legalParameters;
    
    public LegalParametersForm(LegalParameters legalParameters) {
        
        this.legalParameters = legalParameters;
        
        grid = new Grid<>();
        grid.addColumn(g -> g.issueType.displayName).setCaption("");
        grid.addColumn(g -> g.inProgressLevelButton, new ComponentRenderer()).setCaption("In progress").setWidth(110);
        grid.addColumn(g -> g.inHistoryLevelButton, new ComponentRenderer()).setCaption("In history").setWidth(110);
        grid.addColumn(g -> g.limitationYearsCombo, new ComponentRenderer()).setCaption("Limitation").setWidth(110);
        
        grid.setHeightByRows(LegalIssueType.values().length);
        grid.setRowHeight(35);
        
        List<Row> items = Stream.of(LegalIssueType.values())
                .map(issueType -> new Row(issueType, legalParameters.get(issueType).companyEntry))
                .collect(Collectors.toList());
        
        grid.setItems(items);
        grid.setWidth("700px");
        grid.addStyleName(VaadinUtil.GRID_SMALL);
    }

    @Override
    public LegalParameters getValue() {
        return legalParameters;
    }

    @Override
    protected Component initContent() {
        return grid;
    }

    @Override
    protected void doSetValue(LegalParameters legalParameters) {
        throw new IllegalStateException();
    }
    
    static class Row {
        
        final LegalIssueType issueType;
        
        final LevelButton inProgressLevelButton;
        final LevelButton inHistoryLevelButton;
        final ComboBox<Integer> limitationYearsCombo = new ComboBox<>(null, generateComboValues());
        
        public Row(LegalIssueType issueType, EvaluationEntry evaluationEntry) {
            this.issueType = issueType;
            inProgressLevelButton = new LevelButton(evaluationEntry.inProgressLevel);
            inHistoryLevelButton = new LevelButton(evaluationEntry.inHistoryLevel);
            limitationYearsCombo.setValue(evaluationEntry.limitationYears);
            
            limitationYearsCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
            limitationYearsCombo.setWidth("60px");
            limitationYearsCombo.setEmptySelectionAllowed(false);
        }

        private static List<Integer> generateComboValues() {
            return IntStream.range(1, 101).mapToObj(i -> i).collect(Collectors.toList());
        }
    }

}
