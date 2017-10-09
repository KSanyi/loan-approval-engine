package hu.lae.infrastructure.ui.loancalculation.decision;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

import hu.lae.domain.finance.FinancialHistory;
import hu.lae.infrastructure.ui.VaadinUtil;

@SuppressWarnings("serial")
class EbitdaTable extends Grid<EbitdaTableRow> {

    EbitdaTable(FinancialHistory financialHistory) {
        List<Integer> years = financialHistory.years();
        
        addColumn(row -> row.caption).setCaption("");
        years.stream().forEach(year -> addColumn(row -> row.ebitdas.get(year)).setCaption(year + "").setStyleGenerator(item -> "v-align-right"));
        addColumn(row -> row.lastChange, new NumberRenderer(new DecimalFormat("0.0%"))).setCaption("Last growth").setStyleGenerator(item -> "v-align-right");
        
        Map<Integer, Long> ebitdas = financialHistory.incomeStatementHistory().ebitdas();
        Map<Integer, Long> sales = financialHistory.incomeStatementHistory().sales();
        
        List<EbitdaTableRow> items = Arrays.asList(
                new EbitdaTableRow("EBITDA",ebitdas, financialHistory.incomeStatementHistory().ebitdaGrowth()),
                new EbitdaTableRow("Sales", sales, financialHistory.incomeStatementHistory().salesGrowth()));
        setItems(items);
        
        addStyleName(VaadinUtil.GRID_SMALL);
        setCaption("EBITDA & Sales & Free CF");
        setHeightMode(HeightMode.ROW);
        setHeightByRows(2);
    }
    
}

class EbitdaTableRow {
    
    final String caption;
    final Map<Integer, Long> ebitdas;
    final double lastChange;

    public EbitdaTableRow(String caption, Map<Integer, Long> ebitdas, double lastChange) {
        this.caption = caption;
        this.ebitdas = ebitdas;
        this.lastChange = lastChange;
    }
}