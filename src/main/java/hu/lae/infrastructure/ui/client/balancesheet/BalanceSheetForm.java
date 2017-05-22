package hu.lae.infrastructure.ui.client.balancesheet;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.accounting.BalanceSheet;
import hu.lae.accounting.BalanceSheet.Assets;
import hu.lae.accounting.BalanceSheet.Liabilities;
import hu.lae.infrastructure.ui.component.AmountField;

@SuppressWarnings("serial")
class BalanceSheetForm extends HorizontalLayout {

    private final AmountField arField = new AmountField("Accounts R");
    private final AmountField stockField = new AmountField("Stock");
    private final AmountField cashField = new AmountField("Cash");
    private final AmountField otherAssetsField = new AmountField("Other");
    
    private final AmountField ownEquityField = new AmountField("Own equity");
    private final AmountField evReserveField = new AmountField("Evaluation reserve");
    private final AmountField apField = new AmountField("Accounts P");
    private final AmountField otherLiabilitiesField = new AmountField("Other");
    private final AmountField totalField = new AmountField("Total");
    
    BalanceSheetForm(BalanceSheet balanceSheet) {
        addComponents(createAssetsLayout(balanceSheet.assets), createLiabilitiesLayout(balanceSheet.liabilities));
        setMargin(true);
    }
    
    BalanceSheet getBalanceSheet() {
        return new BalanceSheet(new Assets(arField.getAmount(), stockField.getAmount(), cashField.getAmount(), otherAssetsField.getAmount()), 
                new Liabilities(ownEquityField.getAmount(), evReserveField.getAmount(), apField.getAmount(), otherLiabilitiesField.getAmount(), totalField.getAmount()));
    }
    
    private Layout createAssetsLayout(Assets assets) {
        
        arField.setAmount(assets.accountsReceivable);
        stockField.setAmount(assets.stock);
        cashField.setAmount(assets.cash);
        otherAssetsField.setAmount(assets.other);
        
        Label header = new Label("Assets");
        header.addStyleName(ValoTheme.LABEL_H4);
        FormLayout layout = new FormLayout(header, arField, stockField, cashField, otherAssetsField);
        layout.setMargin(false);
        layout.setSpacing(false);

        return layout;
    }
    
    private Layout createLiabilitiesLayout(Liabilities liabilities) {
        
        ownEquityField.setAmount(liabilities.ownEquity);
        evReserveField.setAmount(liabilities.evaluationReserve);
        apField.setAmount(liabilities.accountsPayable);
        otherLiabilitiesField.setAmount(liabilities.otherLiabilities);
        totalField.setAmount(liabilities.total);
        
        Label header = new Label("Liabilities");
        header.addStyleName(ValoTheme.LABEL_H4);
        FormLayout layout = new FormLayout(header, ownEquityField, evReserveField, apField, otherLiabilitiesField, totalField);
        layout.setMargin(false);
        layout.setSpacing(false);
        return layout;
    }
    
}
