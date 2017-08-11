package hu.lae.infrastructure.ui.loancalculation.proposal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Panel;
import com.vaadin.ui.renderers.HtmlRenderer;

import hu.lae.domain.loan.ExistingLoan;
import hu.lae.domain.loan.ExistingLoans;
import hu.lae.domain.loan.ExistingLoansRefinancing;
import hu.lae.domain.riskparameters.InterestRate;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Clock;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class ExistingLoansRefinancingTable extends CustomField<ExistingLoansRefinancing> {

    private final Grid<ExistingLoan> grid;
    
    private ExistingLoans existingLoans;
    
    private final List<RefinanceChangeListener> refinanceChangeListeners = new ArrayList<>();
    
    private final InterestRate longTermInterestRate;
    
    private final InterestRate shortTermInterestRate;
    
    ExistingLoansRefinancingTable(ExistingLoans existingLoans, InterestRate longTermInterestRate, InterestRate shortTermInterestRate) {
        this.existingLoans = existingLoans;
        this.longTermInterestRate = longTermInterestRate;
        this.shortTermInterestRate = shortTermInterestRate;
        grid = new Grid<>();
    }
    
    @Override
    public ExistingLoansRefinancing getValue() {
        return new ExistingLoansRefinancing(existingLoans.existingLoans.stream()
                .collect(Collectors.toMap(Function.identity(), grid.getSelectedItems()::contains)));
    }

    @Override
    protected Component initContent() {
        grid.addColumn(l -> l.type.toString()).setCaption("Type");
        
        grid.addColumn(l -> Formatters.formatAmount(l.amount))
            .setCaption("Amount")
            .setWidth(90)
            .setStyleGenerator(item -> "v-align-right");
        
        grid.addColumn(l -> l.isLocal ? VaadinIcons.HOME.getHtml() : "")
            .setCaption("Erste?")
            .setRenderer(new HtmlRenderer())
            .setWidth(80)
            .setStyleGenerator(item -> "v-align-center");
        
        grid.addColumn(l -> l.expiry.map(LocalDate::toString).orElse(""))
            .setCaption("Expiry")
            .setWidth(120);
        
        grid.addColumn(l -> Formatters.formatAmount(l.calculateYearlyDebtService(shortTermInterestRate, longTermInterestRate, Clock.date())))
            .setCaption("Debt service")
            .setWidth(120)
            .setStyleGenerator(item -> "v-align-right");
        
        grid.setWidth("600px");
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addSelectionListener(v -> refinanceChangeListeners.stream().forEach(l -> l.changeHappened()));
        
        grid.setItems(existingLoans.existingLoans);
        
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
