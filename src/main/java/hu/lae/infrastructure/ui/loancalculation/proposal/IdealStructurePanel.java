package hu.lae.infrastructure.ui.loancalculation.proposal;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.domain.loan.LoanRequest;
import hu.lae.infrastructure.ui.component.AmountField;

@SuppressWarnings("serial")
class IdealStructurePanel extends Panel {

    private final AmountField stLoanField = new AmountField(null);
    
    private final AmountField ltLoanField = new AmountField(null);
    
    private final AmountField sumLoanField = new AmountField(null);
    
    public IdealStructurePanel(LoanRequest idealLoanRequest, int idealLoanDuration) {
        
        Label stlabel = new Label("Ideal short term loan");
        stlabel.setWidth("150px");
        stLoanField.setWidth("60px");
        stLoanField.setReadOnly(true);
        HorizontalLayout stLayout = new HorizontalLayout(stlabel, stLoanField);

        Label ltlabel = new Label("Ideal long term loan");
        ltlabel.setWidth("150px");
        ltLoanField.setWidth("60px");
        ltLoanField.setReadOnly(true);
        HorizontalLayout ltLayout = new HorizontalLayout(ltlabel, ltLoanField);
        
        Label sumLabel = new Label("Max debt capacity");
        sumLabel.setWidth("150px");
        sumLoanField.setWidth("60px");
        sumLoanField.setReadOnly(true);
        HorizontalLayout sumLayout = new HorizontalLayout(sumLabel, sumLoanField);
                
        VerticalLayout layout = new VerticalLayout(stLayout, ltLayout, sumLayout);
        
        setContent(layout);
        setCaption("Ideal structure for " + idealLoanDuration + " years");
        setSizeUndefined();
        
        update(idealLoanRequest);
    }
    
    public void update(LoanRequest idealLoanRequest) {
        stLoanField.setAmount((long)idealLoanRequest.shortTermLoan);
        ltLoanField.setAmount((long)idealLoanRequest.longTermLoan);
        sumLoanField.setAmount((long)idealLoanRequest.sum());
    }
    
}
