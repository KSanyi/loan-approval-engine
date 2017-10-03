package hu.lae.infrastructure.ui.client.finstatement;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;

import hu.lae.domain.finance.FinancialStatementData;
import hu.lae.infrastructure.ui.client.finstatement.balancesheet.BalanceSheetPanel;
import hu.lae.infrastructure.ui.client.finstatement.incomestatement.IncomeStatementPanel;

@SuppressWarnings("serial")
public class FinancialStatementForm extends CustomField<FinancialStatementData>{

    private final BalanceSheetPanel balanceSheetPanel;
    
    private final IncomeStatementPanel incomeStatementPanel;
    
    private final int year;
    
    public FinancialStatementForm(FinancialStatementData financialStatementData) {
        year = financialStatementData.year;
        balanceSheetPanel = new BalanceSheetPanel(financialStatementData.balanceSheet);
        incomeStatementPanel = new IncomeStatementPanel(financialStatementData.incomeStatement);
    }

    @Override
    public FinancialStatementData getValue() {
        return new FinancialStatementData(year, balanceSheetPanel.getBalanceSheet(), incomeStatementPanel.getIncomeStatement());
    }

    @Override
    protected Component initContent() {
        HorizontalLayout layout = new HorizontalLayout(balanceSheetPanel, incomeStatementPanel);
        return layout;
    }

    @Override
    protected void doSetValue(FinancialStatementData value) {
        throw new IllegalStateException();
    }

}
