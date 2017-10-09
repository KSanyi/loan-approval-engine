package hu.lae.infrastructure.ui.loancalculation.decision;

import java.util.Arrays;
import java.util.List;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;

import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class EquityRatioTable extends Grid<EquityRatioTableRow>{

    EquityRatioTable(double equityRatioBeforeLoan, double equityRatioAfterLoan) {
        addColumn(row -> "Equity ratio").setCaption("");
        addColumn(row -> row.beforeLoan).setCaption("Before loan").setStyleGenerator(item -> "v-align-right");
        addColumn(row -> row.afterLoan).setCaption("After loan").setStyleGenerator(item -> "v-align-right");
        
        List<EquityRatioTableRow> items = Arrays.asList(
                new EquityRatioTableRow(Formatters.formatPercent(equityRatioBeforeLoan), Formatters.formatPercent(equityRatioAfterLoan)));
        setItems(items);
        
        addStyleName(VaadinUtil.GRID_SMALL);
        setHeightMode(HeightMode.ROW);
        setHeightByRows(1);
    }
    
}

class EquityRatioTableRow {
    
    final String beforeLoan;
    final String afterLoan;

    public EquityRatioTableRow(String beforeLoan, String afterLoan) {
        this.beforeLoan = beforeLoan;
        this.afterLoan = afterLoan;
    }
}
