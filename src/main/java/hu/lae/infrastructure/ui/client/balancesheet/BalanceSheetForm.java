package hu.lae.infrastructure.ui.client.balancesheet;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.accounting.BalanceSheet;
import hu.lae.domain.accounting.BalanceSheet.Assets;
import hu.lae.domain.accounting.BalanceSheet.Liabilities;
import hu.lae.infrastructure.ui.component.AmountField;

@SuppressWarnings("serial")
class BalanceSheetForm extends CustomField<BalanceSheet> {

    private final AmountField arField = new AmountField("Accounts R");
    private final AmountField stockField = new AmountField("Stock");
    private final AmountField cashField = new AmountField("Cash");
    private final AmountField otherAssetsField = new AmountField("Other");
    
    private final AmountField ownEquityField = new AmountField("Own equity");
    private final AmountField evReserveField = new AmountField("Evaluation reserve");
    private final AmountField apField = new AmountField("Accounts P");
    private final AmountField otherLiabilitiesField = new AmountField("Other");
    private final AmountField totalField = new AmountField("Total");
    
    private final BalanceSheet balanceSheet;
    
    BalanceSheetForm(BalanceSheet balanceSheet) {
        this.balanceSheet = balanceSheet;
    }
    
    private Layout createAssetsLayout() {
        Label header = new Label("Assets");
        header.addStyleName(ValoTheme.LABEL_H4);
        FormLayout layout = new FormLayout(header, arField, stockField, cashField, otherAssetsField);
        layout.setMargin(false);
        layout.setSpacing(false);

        return layout;
    }
    
    private Layout createLiabilitiesLayout() {
        Label header = new Label("Liabilities");
        header.addStyleName(ValoTheme.LABEL_H4);
        FormLayout layout = new FormLayout(header, ownEquityField, evReserveField, apField, otherLiabilitiesField, totalField);
        layout.setMargin(false);
        layout.setSpacing(false);
        return layout;
    }

    @Override
    public BalanceSheet getValue() {
        return new BalanceSheet(balanceSheet.year, new Assets(arField.getAmount(), stockField.getAmount(), cashField.getAmount(), otherAssetsField.getAmount()), 
                new Liabilities(ownEquityField.getAmount(), evReserveField.getAmount(), apField.getAmount(), otherLiabilitiesField.getAmount(), totalField.getAmount()));
    }

    @Override
    protected Component initContent() {
        HorizontalLayout layout = new HorizontalLayout(createAssetsLayout(), createLiabilitiesLayout());
        layout.setMargin(new MarginInfo(false, true, true, true));
        
        doSetValue(balanceSheet);
        
        return layout;
    }

    @Override
    protected void doSetValue(BalanceSheet balanceSheet) {
        arField.setAmount(balanceSheet.assets.accountsReceivable);
        stockField.setAmount(balanceSheet.assets.stock);
        cashField.setAmount(balanceSheet.assets.cash);
        otherAssetsField.setAmount(balanceSheet.assets.other);
     
        ownEquityField.setAmount(balanceSheet.liabilities.ownEquity);
        evReserveField.setAmount(balanceSheet.liabilities.evaluationReserve);
        apField.setAmount(balanceSheet.liabilities.accountsPayable);
        otherLiabilitiesField.setAmount(balanceSheet.liabilities.otherLiabilities);
        totalField.setAmount(balanceSheet.liabilities.total);
    }
    
}
