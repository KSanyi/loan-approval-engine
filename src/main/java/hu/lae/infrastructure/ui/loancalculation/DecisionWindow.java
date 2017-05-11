package hu.lae.infrastructure.ui.loancalculation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import hu.lae.Client;
import hu.lae.infrastructure.ui.VaadinUtil;

@SuppressWarnings("serial")
class DecisionWindow extends Window {

    private final Client client;
    
    DecisionWindow(Client client) {
        this.client = client;
        setModal(true);
        setCaption("Decision");
        setContent(createLayout());
        setSizeUndefined();
    }
    
    private Layout createLayout() {
        VerticalLayout layout = new VerticalLayout(
                new HorizontalLayout(createEbitdaTable(), createWarningsTable()));
        return layout;
    }
    
    private Grid<EbitdaTableRow> createEbitdaTable() {
        
        List<Integer> years = client.incomeStatementData.incomeStatements().map(statement -> statement.year).collect(Collectors.toList());
        
        Grid<EbitdaTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("");
        grid.addColumn(row -> row.ebitdaT2).setCaption(years.get(0) + "");
        grid.addColumn(row -> row.ebitdaT1).setCaption(years.get(1) + "");
        grid.addColumn(row -> row.ebitdaT).setCaption(years.get(2) + "");
        grid.addColumn(row -> row.change).setCaption("%");    
        
        List<Long> ebitdas = client.incomeStatementData.ebitdas();
        
        List<EbitdaTableRow> items = Arrays.asList(
                new EbitdaTableRow("EBITDA", ebitdas.get(0), ebitdas.get(1), ebitdas.get(2), 0.09),
                new EbitdaTableRow("Sales", 0, 0, 0, 0.09));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("EBITDA & Sales & Free CF");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(2);
        
        return grid;
    }
    
    private Grid<EbitdaTableRow> createWarningsTable() {
        
        List<Integer> years = client.incomeStatementData.incomeStatements().map(statement -> statement.year).collect(Collectors.toList());
        
        Grid<EbitdaTableRow> grid = new Grid<>();
        grid.addColumn(row -> row.caption).setCaption("");
        grid.addColumn(row -> row.ebitdaT2).setCaption(years.get(0) + "");
        grid.addColumn(row -> row.ebitdaT1).setCaption(years.get(1) + "");
        grid.addColumn(row -> row.ebitdaT).setCaption(years.get(2) + "");
        grid.addColumn(row -> row.change).setCaption("%");    
        
        List<EbitdaTableRow> items = Arrays.asList(
                new EbitdaTableRow("Equity value", 0, 0, 0, 0),
                new EbitdaTableRow("Equity Ratio", 0, 0, 0, 0),
                new EbitdaTableRow("Liquidity ratio", 0, 0, 0, 0),
                new EbitdaTableRow("Suppliers day", 0, 0, 0, 0),
                new EbitdaTableRow("Buyers days", 0, 0, 0, 0),
                new EbitdaTableRow("Stock days", 0, 0, 0, 0));
        grid.setItems(items);
        
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        grid.setCaption("Warnings & KOs");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(6);
        
        return grid;
    }
    
    private static class EbitdaTableRow {
        
        final String caption;
        final long ebitdaT2;
        final long ebitdaT1;
        final long ebitdaT;
        final double change;

        public EbitdaTableRow(String caption, long ebitdaT2, long ebitdaT1, long ebitdaT, double change) {
            this.caption = caption;
            this.ebitdaT2 = ebitdaT2;
            this.ebitdaT1 = ebitdaT1;
            this.ebitdaT = ebitdaT;
            this.change = change;
        }
    }
    
}
