package hu.lae.infrastructure.ui.loancalculation.decision;

import java.util.Arrays;
import java.util.List;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;

import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class FreeCashFlowTable extends Grid<FreeCFTableRow>{

    public FreeCashFlowTable(double lastYearBasedFCF, double averageBasedFCF) {
        addColumn(row -> "Free Cash Flow").setCaption("");
        addColumn(row -> row.averageBasedValue).setCaption("W Corrected").setStyleGenerator(item -> "v-align-right");
        addColumn(row -> row.lastYearBasedValue).setCaption("W Last year").setStyleGenerator(item -> "v-align-right");
        
        List<FreeCFTableRow> items = Arrays.asList(
                new FreeCFTableRow(Formatters.formatAmount(averageBasedFCF), Formatters.formatAmount(lastYearBasedFCF)));
        setItems(items);
        
        addStyleName(VaadinUtil.GRID_SMALL);
        setHeightMode(HeightMode.ROW);
        setHeightByRows(1);
    }
    
}

class FreeCFTableRow {
    
    final String lastYearBasedValue;
    final String averageBasedValue;

    public FreeCFTableRow(String lastYearBasedValue, String averageBasedValue) {
        this.lastYearBasedValue = lastYearBasedValue;
        this.averageBasedValue = averageBasedValue;
    }
}
