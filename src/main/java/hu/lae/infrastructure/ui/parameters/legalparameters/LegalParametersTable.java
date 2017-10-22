package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import hu.lae.infrastructure.ui.component.AmountField;
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
        addColumn(r -> r.materialityThresholdField, new ComponentRenderer()).setCaption("Materiality threshold").setId("m");
        
        items = Stream.of(LegalIssueType.values())
                .map(issueType -> new Row(issueType, legalIssueEvaluationMap.get(issueType).companyEntry, legalIssueEvaluationMap.get(issueType).companyEntry, legalIssueEvaluationMap.get(issueType).materialityThreshold))
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
            column.setResizable(false);
        });
        
        getColumn("0").setWidthUndefined().setExpandRatio(1);
        
        setSelectionMode(SelectionMode.NONE);
        setHeightByRows(LegalIssueType.values().length);
        setRowHeight(35);
        setWidth("1150px");
        addStyleName(VaadinUtil.GRID_SMALL);
        
    }
    
    private void createHeader() {
        
        getDefaultHeaderRow().getCell("m").setHtml("<div title='Materiality threshold'>Materiality threshold</div>");
        
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
    final ComboBox<Optional<Integer>> companyLimitationYearsCombo = createComboBox("Company.Limitation.Years");
    
    final LevelButton groupInProgressLevelButton;
    final LevelButton groupInHistoryLevelButton;
    final ComboBox<Optional<Integer>> groupLimitationYearsCombo = createComboBox("Group.Limitation.Years");
    
    final AmountField materialityThresholdField = new AmountField(null, "Materiality.Threashold");
    
    Row(LegalIssueType issueType, EvaluationEntry companyEvaluationEntry, EvaluationEntry groupEvaluationEntry, Optional<Integer> materialityThreashold) {
        this.issueType = issueType;
        companyInProgressLevelButton = new LevelButton(companyEvaluationEntry.inProgressLevel, "company.inProgress." + issueType);
        companyInHistoryLevelButton = new LevelButton(companyEvaluationEntry.inHistoryLevel, "company.inHistory." + issueType);
        companyLimitationYearsCombo.setValue(companyEvaluationEntry.limitationYears);
        
        groupInProgressLevelButton = new LevelButton(groupEvaluationEntry.inProgressLevel, "group.inProgress." + issueType);
        groupInHistoryLevelButton = new LevelButton(groupEvaluationEntry.inHistoryLevel, "group.inHistory." + issueType);
        groupLimitationYearsCombo.setValue(groupEvaluationEntry.limitationYears);
        
        materialityThresholdField.setWidth("70px");
        
        if(issueType.hasMaterialityThreshold) {
            materialityThresholdField.setAmount((long)materialityThreashold.get());
        } else {
            materialityThresholdField.setVisible(false);
        }
        
    }
    
    LegalIssueEvaluation getLegalIssueEvaluation() {
        if(issueType.hasMaterialityThreshold) {
            return new LegalIssueEvaluation(
                    new EvaluationEntry(companyInProgressLevelButton.getValue(), companyInHistoryLevelButton.getValue(), companyLimitationYearsCombo.getValue()),
                    new EvaluationEntry(groupInProgressLevelButton.getValue(), groupInHistoryLevelButton.getValue(), groupLimitationYearsCombo.getValue()),
                    (int)materialityThresholdField.getAmount());
        } else {
            return new LegalIssueEvaluation(
                    new EvaluationEntry(companyInProgressLevelButton.getValue(), companyInHistoryLevelButton.getValue(), companyLimitationYearsCombo.getValue()),
                    new EvaluationEntry(groupInProgressLevelButton.getValue(), groupInHistoryLevelButton.getValue(), groupLimitationYearsCombo.getValue()));
        }
        
    }

    private static ComboBox<Optional<Integer>> createComboBox(String caption) {
    	List<Optional<Integer>> values = new ArrayList<>();
    	IntStream.range(1, 11).forEach(value -> values.add(Optional.of(value)));
    	values.add(Optional.empty());
    	
    	ComboBox<Optional<Integer>> comboBox = new ComboBox<>(null, "Group.Limitation.Years", values);
    	comboBox.setItemCaptionGenerator(item -> item.isPresent() ? item.get().toString() : "None");
    	
    	comboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
    	comboBox.setWidth("75px");
    	comboBox.setEmptySelectionAllowed(false);
    	comboBox.setDescription("None means no limitation");
    	comboBox.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
    	
        return comboBox;
    }
}
