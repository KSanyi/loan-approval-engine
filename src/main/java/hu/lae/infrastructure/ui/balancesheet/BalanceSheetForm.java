package hu.lae.infrastructure.ui.balancesheet;

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
    
    private final AmountField apField = new AmountField("Accounts P");
    private final AmountField otherLiabilitiesField = new AmountField("Other");
    
    BalanceSheetForm(BalanceSheet balanceSheet) {
        addComponents(createAssetsLayout(balanceSheet.assets), createLiabilitiesLayout(balanceSheet.liabilities));
    }
    
    BalanceSheet getBalanceSheet() {
        return new BalanceSheet(new Assets(arField.getAmount(), stockField.getAmount(), cashField.getAmount(), otherAssetsField.getAmount()), 
                new Liabilities(apField.getAmount(), otherLiabilitiesField.getAmount()));
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

        return layout;
    }
    
    private Layout createLiabilitiesLayout(Liabilities liabilities) {
        
        apField.setAmount(liabilities.accountsPayable);
        otherLiabilitiesField.setAmount(liabilities.otherLiabilities);
        
        Label header = new Label("Liabilities");
        header.addStyleName(ValoTheme.LABEL_H4);
        FormLayout layout = new FormLayout(header, apField, otherLiabilitiesField, new Label(""), new Label(""));
        layout.setMargin(false);
        return layout;
    }
    
}
