package hu.lae.infrastructure.ui.parameters.riskparameters;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.riskparameters.RiskParametersRepository;
import hu.lae.infrastructure.ui.component.Button;

@SuppressWarnings("serial")
public class RiskParametersScreen extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
	private final RiskParametersRepository riskParameterRepository;
    
    private final Button saveButton = new Button("Save", click -> save());
    
    private final RiskParametersForm riskParametersForm;
	
	public RiskParametersScreen(RiskParametersRepository riskParameterRepository) {
		
		this.riskParameterRepository = riskParameterRepository;
        
        this.riskParametersForm = new RiskParametersForm(riskParameterRepository.loadRiskParameters());
		
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        addComponents(riskParametersForm, saveButton);
        
        setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
	}

	private void save() {
        riskParameterRepository.updateRiskParameters(riskParametersForm.getValue());
        logger.info("Legal parameters saved");
        Notification.show("Risk parameters updated");
    }
	
}
