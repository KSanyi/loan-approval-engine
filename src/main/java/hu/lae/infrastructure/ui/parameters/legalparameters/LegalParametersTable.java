package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;

import hu.lae.domain.legal.LegalIssueEvaluation;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.infrastructure.ui.VaadinUtil;

@SuppressWarnings("serial")
class LegalParametersTable extends Grid<LegalParametersTableRow> {

    private List<LegalParametersTableRow> items;
    
    LegalParametersTable(List<LegalIssueEvaluation> legalIssueEvaluationList) {
        
        addColumn(r -> r.issueType.displayName).setSortable(false).setCaption("").setId("0");
        addColumn(r -> r.companyInProgressLevelButton, new ComponentRenderer()).setCaption("In progress").setId("c1");
        addColumn(r -> r.companyInHistoryLevelButton, new ComponentRenderer()).setCaption("In history").setId("c2");
        addColumn(r -> r.companyLimitationYearsCombo, new ComponentRenderer()).setCaption("Limitation").setId("c3");
        addColumn(r -> r.groupInProgressLevelButton, new ComponentRenderer()).setCaption("In progress").setId("g1");
        addColumn(r -> r.groupInHistoryLevelButton, new ComponentRenderer()).setCaption("In history").setId("g2");
        addColumn(r -> r.groupLimitationYearsCombo, new ComponentRenderer()).setCaption("Limitation").setId("g3");
        addColumn(r -> r.materialityThresholdField, new ComponentRenderer()).setCaption("Materiality threshold").setId("m");
        
        items = legalIssueEvaluationList.stream()
                .map(evaluation -> new LegalParametersTableRow(evaluation.legalIssueType, evaluation.companyEntry, evaluation.companyGroupEntry, evaluation.materialityThreshold))
                .collect(Collectors.toList());
        
        setItems(items);
        
        format();
    }
    
    List<LegalIssueEvaluation> getLegalIssueEvaluationList() {
        return items.stream().map(LegalParametersTableRow::getLegalIssueEvaluation).collect(Collectors.toList());
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

