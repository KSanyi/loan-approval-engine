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
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class ExistingLoansRefinancingTable extends CustomField<ExistingLoansRefinancing> {

    private final Grid<ExistingLoan> grid;
    
    private ExistingLoans existingLoans;
    
    private final List<RefinanceChangeListener> refinanceChangeListeners = new ArrayList<>();
    
    ExistingLoansRefinancingTable(ExistingLoans existingLoans) {
        this.existingLoans = existingLoans;
        grid = new Grid<>();
        grid.addSelectionListener(v -> refinanceChangeListeners.stream().forEach(l -> l.changeHappened()));
    }
    
    @Override
    public ExistingLoansRefinancing getValue() {
        return new ExistingLoansRefinancing(existingLoans.existingLoans.stream()
                .collect(Collectors.toMap(Function.identity(), grid.getSelectedItems()::contains)));
    }

    @Override
    protected Component initContent() {
        grid.addColumn(l -> l.type.toString()).setCaption("Tipus");
        grid.addColumn(l -> Formatters.formateAmount(l.amount)).setCaption("Összeg").setWidth(90).setStyleGenerator(item -> "v-align-right");
        grid.addColumn(l -> l.isLocal ? VaadinIcons.HOME.getHtml() : "").setCaption("Erstés").setRenderer(new HtmlRenderer()).setWidth(80).setStyleGenerator(item -> "v-align-center");
        grid.addColumn(l -> l.expiry.map(LocalDate::toString).orElse("")).setCaption("Lejárat").setWidth(120);

        grid.setSelectionMode(SelectionMode.MULTI);
        
        grid.setItems(existingLoans.existingLoans);
        
        grid.setHeightByRows(existingLoans.existingLoans.size());
        grid.addStyleName(VaadinUtil.GRID_SMALL);
        
        grid.setDescription("Select loans for refinancing");
        
        Panel panel = new Panel("Exising loans", grid);
        
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
