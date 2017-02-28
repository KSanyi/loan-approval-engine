package hu.lae.infrastructure.ui.riskparameters;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.riskparameters.RiskParameterRepository;

@SuppressWarnings("serial")
public class RiskParametersPanel extends Panel {

    private final Button updateButton = new Button("Update parameters");
    
    public RiskParametersPanel(RiskParameterRepository riskParameterRepository) {
        setCaption("Risk parameters");
        setWidth("280px");
        
        RiskParametersForm form = new RiskParametersForm(riskParameterRepository.loadRiskParameters());
        updateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        updateButton.addStyleName(ValoTheme.BUTTON_SMALL);
        updateButton.addClickListener(click -> riskParameterRepository.updateRiskParameters(form.getRiskParameters()));
        VerticalLayout layout = new VerticalLayout(form);
        layout.setMargin(true);
        //layout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
        addStyleName("colored");
        setContent(layout);
    }
    
}
