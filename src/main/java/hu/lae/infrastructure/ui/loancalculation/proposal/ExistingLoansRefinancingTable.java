package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Panel;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import hu.lae.domain.loan.ExistingLoan;
import hu.lae.domain.loan.ExistingLoans;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.riskparameters.InterestRates;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.infrastructure.ui.component.CheckBox;
import hu.lae.util.Clock;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class ExistingLoansRefinancingTable extends CustomField<ExistingLoansRefinancing> {

    private final Grid<ExistingLoanRow> grid;
    
    private ExistingLoans existingLoans;
    
    private final List<ExistingLoanRow> rows;
    
    private final List<RefinanceChangeListener> refinanceChangeListeners = new ArrayList<>();
    
    private final InterestRates interestRates;
    
    ExistingLoansRefinancingTable(ExistingLoans existingLoans, InterestRates interestRates) {
        this.existingLoans = existingLoans;
        this.interestRates = interestRates;
        grid = new Grid<>();
        rows = existingLoans.existingLoans.stream().map(ExistingLoanRow::new).collect(Collectors.toList());
    }
    
    @Override
    public ExistingLoansRefinancing getValue() {
        return new ExistingLoansRefinancing(rows.stream()
                .collect(Collectors.toMap(row -> row.existingLoan, row -> row.refinanced.getValue())));
    }

    @Override
    protected Component initContent() {
        
        grid.addColumn(l -> l.refinanced, new ComponentRenderer())
            .setCaption("Refinance")
            .setWidth(100)
            .setStyleGenerator(item -> "v-align-center");
        
        grid.addColumn(l -> l.existingLoan.type.toString()).setCaption("Type");
        
        grid.addColumn(l -> Formatters.formatAmount(l.existingLoan.amount))
            .setCaption("Amount")
            .setWidth(90)
            .setStyleGenerator(item -> "v-align-right");
        
        grid.addColumn(l -> l.existingLoan.isOwn ? VaadinIcons.HOME.getHtml() : "")
            .setCaption("Own")
            .setRenderer(new HtmlRenderer())
            .setWidth(80)
            .setStyleGenerator(item -> "v-align-center");
        
        grid.addColumn(l -> l.existingLoan.expiry.map(LocalDate::toString).orElse(""))
            .setCaption("Expiry")
            .setWidth(120);
        
        grid.addColumn(l -> Formatters.formatAmount(l.existingLoan.calculateYearlyDebtService(interestRates, Clock.date())))
            .setCaption("Debt service")
            .setWidth(120)
            .setStyleGenerator(item -> "v-align-right");

        grid.setWidth("620px");
        grid.setSelectionMode(SelectionMode.NONE);
        
        grid.getColumns().stream().forEach(column -> {
            column.setResizable(false);
            column.setSortable(false);
        });
        
        grid.setItems(rows);
        rows.stream().forEach(row -> row.refinanced.addValueChangeListener(v -> refinanceChangeListeners.stream().forEach(l -> l.changeHappened())));
        
        grid.setHeightByRows(Math.max(1, existingLoans.existingLoans.size()));
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        
        grid.setDescription("Select loans for refinancing");
        
        Panel panel = new Panel("Existing loans", grid);
        
        return panel;
    }
    
    @Override
    protected void doSetValue(ExistingLoansRefinancing value) {
        throw new UnsupportedOperationException();
    }
    
    public void addRefinanceChangeListener(RefinanceChangeListener listener) {
        refinanceChangeListeners.add(listener);
    }
    
    static interface RefinanceChangeListener {
        void changeHappened();
    }
    
}

class ExistingLoanRow {
    
    final CheckBox refinanced = new CheckBox(null, "existing.loan.refinance");
    
    final ExistingLoan existingLoan;

    public ExistingLoanRow(ExistingLoan existingLoan) {
        this.existingLoan = existingLoan;
    }
    
}
