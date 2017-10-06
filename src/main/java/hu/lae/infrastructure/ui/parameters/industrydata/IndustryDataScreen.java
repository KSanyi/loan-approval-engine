package hu.lae.infrastructure.ui.parameters.industrydata;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.industry.IndustryDataRepository;
import hu.lae.infrastructure.ui.component.Button;

@SuppressWarnings("serial")
public class IndustryDataScreen extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
	private final IndustryDataRepository industryDataRepository;
    
    private final Button saveButton = new Button("Save", click -> save());
    
    private final IndustryDataForm industryDataForm;
	
	public IndustryDataScreen(IndustryDataRepository industryDataRepository) {
		
		this.industryDataRepository = industryDataRepository;
        
        this.industryDataForm = new IndustryDataForm(industryDataRepository.loadIndustryData());
		
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        addComponents(industryDataForm, saveButton);
        
        setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
	}

	private void save() {
		industryDataRepository.updateIndustryData(industryDataForm.getValue());
		logger.info("Legal data saved");
        Notification.show("Risk parameters updated");
    }
	
}
