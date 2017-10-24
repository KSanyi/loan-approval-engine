package hu.lae.infrastructure.ui.client.legaldata;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.lae.domain.legal.LegalData;

@SuppressWarnings("serial")
public class LegalDataPanel extends Panel {

    private final LegalDataForm form;
    
    public LegalDataPanel(LegalData legalData) {
        setCaption("Legal data");
        form = new LegalDataForm(legalData);
        
        setContent(new VerticalLayout(form));
    }
    
    public LegalData getLegalData() {
        return form.getValue();
    }
    
}
