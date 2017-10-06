package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import hu.lae.infrastructure.ui.component.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalIssueEvaluation;
import hu.lae.domain.legal.LegalIssueEvaluation.EvaluationEntry;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.infrastructure.ui.VaadinUtil;

@SuppressWarnings("serial")
class LegalParametersTable extends Grid<Row> {

    private List<Row> items;
    
    LegalParametersTable(Map<LegalIssueType, LegalIssueEvaluation> legalIssueEvaluationMap) {
        
        addColumn(r -> r.issueType.displayName).setSortable(false).setCaption("").setId("0");
        addColumn(r -> r.companyInProgressLevelButton, new ComponentRenderer()).setCaption("In progress").setId("c1");
        addColumn(r -> r.companyInHistoryLevelButton, new ComponentRenderer()).setCaption("In history").setId("c2");
        addColumn(r -> r.companyLimitationYearsCombo, new ComponentRenderer()).setCaption("Limitation").setId("c3");
        addColumn(r -> r.groupInProgressLevelButton, new ComponentRenderer()).setCaption("In progress").setId("g1");
        addColumn(r -> r.groupInHistoryLevelButton, new ComponentRenderer()).setCaption("In history").setId("g2");
        addColumn(r -> r.groupLimitationYearsCombo, new ComponentRenderer()).setCaption("Limitation").setId("g3");

        items = Stream.of(LegalIssueType.values())
                .map(issueType -> new Row(issueType, legalIssueEvaluationMap.get(issueType).companyEntry, legalIssueEvaluationMap.get(issueType).companyEntry))
                .collect(Collectors.toList());
        
        setItems(items);
        
        format();
    }
    
    Map<LegalIssueType, LegalIssueEvaluation> getLegalIssueEvaluationMap() {
        Map<LegalIssueType, LegalIssueEvaluation> legalIssueEvaluationMap = new HashMap<>();
        for(Row item : items) {
            legalIssueEvaluationMap.put(item.issueType, item.getLegalIssueEvaluation());
        }
        
        return legalIssueEvaluationMap;
    }
    
    private void format() {

        createHeader();
        
        getColumns().stream().forEach(column -> {
            column.setSortable(false);
            column.setWidth(110);
        });
        
        getColumn("0").setWidthUndefined().setExpandRatio(1);
        
        setSelectionMode(SelectionMode.NONE);
        setHeightByRows(LegalIssueType.values().length);
        setRowHeight(35);
        setWidth("1000px");
        addStyleName(VaadinUtil.GRID_SMALL);
        
    }
    
    private void createHeader() {
        HeaderRow groupingHeader = prependHeaderRow();
        
        HeaderCell companyHeader = groupingHeader.join(
                groupingHeader.getCell("c1"),
                groupingHeader.getCell("c2"),
                groupingHeader.getCell("c3"));
        companyHeader.setHtml("<center>Company</center>");
        
        HeaderCell groupHeader = groupingHeader.join(
                groupingHeader.getCell("g1"),
                groupingHeader.getCell("g2"),
                groupingHeader.getCell("g3"));
        groupHeader.setHtml("<center>Company Group</center>");
    }
    
}

class Row {
    
    final LegalIssueType issueType;
    
    final LevelButton companyInProgressLevelButton;
    final LevelButton companyInHistoryLevelButton;
    final ComboBox<Integer> companyLimitationYearsCombo = new ComboBox<>(null, generateComboValues());
    
    final LevelButton groupInProgressLevelButton;
    final LevelButton groupInHistoryLevelButton;
    final ComboBox<Integer> groupLimitationYearsCombo = new ComboBox<>(null, generateComboValues());
    
    Row(LegalIssueType issueType, EvaluationEntry companyEvaluationEntry, EvaluationEntry groupEvaluationEntry) {
        this.issueType = issueType;
        companyInProgressLevelButton = new LevelButton(companyEvaluationEntry.inProgressLevel, "company.inProgress." + issueType);
        companyInHistoryLevelButton = new LevelButton(companyEvaluationEntry.inHistoryLevel, "company.inHistory." + issueType);
        companyLimitationYearsCombo.setValue(companyEvaluationEntry.limitationYears);
        
        groupInProgressLevelButton = new LevelButton(groupEvaluationEntry.inProgressLevel, "group.inProgress." + issueType);
        groupInHistoryLevelButton = new LevelButton(groupEvaluationEntry.inHistoryLevel, "group.inHistory." + issueType);
        groupLimitationYearsCombo.setValue(groupEvaluationEntry.limitationYears);
        
        companyLimitationYearsCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        companyLimitationYearsCombo.setWidth("60px");
        companyLimitationYearsCombo.setEmptySelectionAllowed(false);
        
        groupLimitationYearsCombo.addStyleName(ValoTheme.COMBOBOX_SMALL);
        groupLimitationYearsCombo.setWidth("60px");
        groupLimitationYearsCombo.setEmptySelectionAllowed(false);
    }
    
    LegalIssueEvaluation getLegalIssueEvaluation() {
        return new LegalIssueEvaluation(
                new EvaluationEntry(companyInProgressLevelButton.getValue(), companyInHistoryLevelButton.getValue(), companyLimitationYearsCombo.getValue()),
                new EvaluationEntry(groupInProgressLevelButton.getValue(), groupInHistoryLevelButton.getValue(), groupLimitationYearsCombo.getValue()), 0);
    }

    private static List<Integer> generateComboValues() {
        return IntStream.range(1, 101).mapToObj(i -> i).collect(Collectors.toList());
    }
}
