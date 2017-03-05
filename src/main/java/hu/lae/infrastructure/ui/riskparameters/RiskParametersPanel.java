package hu.lae.infrastructure.ui.riskparameters;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.riskparameters.RiskParameterRepository;
import hu.lae.riskparameters.RiskParameters;

@SuppressWarnings("serial")
public class RiskParametersPanel extends Panel {

    private final RiskParametersForm riskParametersForm;
    
    private final Button updateButton = new Button("Update parameters");
    
    public RiskParametersPanel(RiskParameterRepository riskParameterRepository) {
        setCaption("Risk parameters");
        setWidth("280px");
        
        riskParametersForm = new RiskParametersForm(riskParameterRepository.loadRiskParameters());
        updateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        updateButton.addStyleName(ValoTheme.BUTTON_SMALL);
        updateButton.addClickListener(click -> riskParameterRepository.updateRiskParameters(riskParametersForm.getRiskParameters()));
        VerticalLayout layout = new VerticalLayout(riskParametersForm);
        layout.setMargin(true);
        //layout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
        addStyleName("colored");
        setContent(layout);
    }
    
    public RiskParameters getRiskParameters() {
        return riskParametersForm.getRiskParameters();
    }
    
}
