package hu.lae.infrastructure.ui.loancalculation.decision;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import hu.lae.domain.loan.Loan;
import hu.lae.infrastructure.ui.VaadinUtil;
import hu.lae.util.Formatters;

@SuppressWarnings("serial")
class AllLoansTable extends Grid<Loan> {

    AllLoansTable(List<Loan> loans) {
        addColumn(l -> l.isOwn ? VaadinIcons.HOME.getHtml() : "").setCaption("Új").setRenderer(new HtmlRenderer()).setWidth(80).setStyleGenerator(item -> "v-align-center");
        addColumn(l -> l.loanType.name()).setCaption("Tipus");
        addColumn(l -> Formatters.formatAmount(l.amount)).setCaption("Összeg").setWidth(90).setStyleGenerator(item -> "v-align-right");
        addColumn(l -> l.isOwn ? VaadinIcons.HOME.getHtml() : "").setCaption("Erstés").setRenderer(new HtmlRenderer()).setWidth(80).setStyleGenerator(item -> "v-align-center");
        addColumn(l -> l.isOwn ? VaadinIcons.STAR.getHtml() : "").setCaption("Új").setRenderer(new HtmlRenderer()).setWidth(80).setStyleGenerator(item -> "v-align-center");

        setSelectionMode(SelectionMode.NONE);
        
        setItems(loans);
        
        setHeightByRows(loans.size());
        addStyleName(VaadinUtil.GRID_SMALL);
    }

}
