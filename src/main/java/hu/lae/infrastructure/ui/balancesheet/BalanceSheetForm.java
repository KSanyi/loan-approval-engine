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

    private final AmountField arField = new AmountField("Accounts receivable");
    private final AmountField stockField = new AmountField("Stock");
    private final AmountField cashField = new AmountField("Cash");
    private final AmountField otherAssetsField = new AmountField("Other");
    private final AmountField assetsSumField = new AmountField("Sum");
    
    private final AmountField apField = new AmountField("Accounts payable");
    private final AmountField otherLiabilitiesField = new AmountField("Other");
    private final AmountField liabilitiesSumField = new AmountField("Sum");
    
    BalanceSheetForm(BalanceSheet balanceSheet) {
        setSpacing(true);
        addComponents(createAssetsLayout(balanceSheet.assets), createLiabilitiesLayout(balanceSheet.liabilities));
    }
    
    BalanceSheet getBalanceSheet() {
        return new BalanceSheet(new Assets(arField.getAmount(), stockField.getAmount(), cashField.getAmount(), otherAssetsField.getAmount()), 
                new Liabilities(apField.getAmount(), otherLiabilitiesField.getAmount()));
    }
    
    private Layout createAssetsLayout(Assets assets) {
        Label header = new Label("Assets");
        header.addStyleName(ValoTheme.LABEL_H4);
        assetsSumField.setReadOnly(true);
        assetsSumField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        FormLayout layout = new FormLayout(header, arField, stockField, cashField, otherAssetsField);
        layout.setSpacing(false);
        layout.setMargin(false);

        return layout;
    }
    
    private Layout createLiabilitiesLayout(Liabilities liabilities) {
        Label header = new Label("Liabilities");
        header.addStyleName(ValoTheme.LABEL_H4);
        liabilitiesSumField.setReadOnly(true);
        liabilitiesSumField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        FormLayout layout = new FormLayout(header, apField, otherLiabilitiesField, new Label(""), new Label(""));
        layout.setSpacing(false);
        layout.setMargin(false);
        return layout;
    }
    
}
