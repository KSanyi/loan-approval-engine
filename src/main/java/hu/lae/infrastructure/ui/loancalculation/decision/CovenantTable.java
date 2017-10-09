package hu.lae.infrastructure.ui.loancalculation.decision;

import java.util.Arrays;
import java.util.List;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;

import hu.lae.domain.riskparameters.Thresholds;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class CovenantTable extends Grid<CovenantTableRow>{

    CovenantTable(double turnoverReqValue, double debtCapacityUsage, double localLoansRatio, double allLocalLoans, String collateralRequirement, Thresholds thresholds) {
        addColumn(row -> row.caption).setCaption("Covenant");
        addColumn(row -> row.value).setCaption("Value").setStyleGenerator(item -> "v-align-right");
        addColumn(row -> row.message).setCaption("Contractual covenant");
        
        boolean furtherIndebtnessOk = debtCapacityUsage <= thresholds.debtCapacity;
        boolean localLoansRatioOk = localLoansRatio <= thresholds.localLoanRatio;
        
        List<CovenantTableRow> items = Arrays.asList(
                new CovenantTableRow("Turnover requirement", "", "Min HUF " + Formatters.formatAmount(turnoverReqValue) + " mln turnover"),
                new CovenantTableRow("Further indebtedness", Formatters.formatPercent(debtCapacityUsage), furtherIndebtnessOk ? "-" : "No further indebtedness"),
                new CovenantTableRow("Account opening clause", Formatters.formatDecimal(localLoansRatio), localLoansRatioOk ? "No restriction" : "Further accounts w bank consent"),
                new CovenantTableRow("Collateral requirement", "", collateralRequirement));

        setItems(items);
        
        addStyleName(VaadinUtil.GRID_SMALL);
        setCaption("Covenants");
        setHeightMode(HeightMode.ROW);
        setHeightByRows(items.size());
        setWidth("700px");
    }
    
}

class CovenantTableRow {
    
    final String caption;
    final String value;
    final String message;

    public CovenantTableRow(String caption, String value, String message) {
        this.caption = caption;
        this.value = value;
        this.message = message;
    }
}
