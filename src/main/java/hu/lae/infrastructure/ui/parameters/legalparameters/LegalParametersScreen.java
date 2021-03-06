package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalParametersRepository;
import hu.lae.infrastructure.ui.component.Button;

@SuppressWarnings("serial")
public class LegalParametersScreen extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final LegalParametersRepository legalParametersRepository;
    
    private final Button saveButton = new Button("Save", click -> save());
    
    private final LegalParametersForm legalParametersForm;
    
    public LegalParametersScreen(LegalParametersRepository legalParametersRepository) {
        
        this.legalParametersRepository = legalParametersRepository;
        
        this.legalParametersForm = new LegalParametersForm(legalParametersRepository.loadLegalParameters());
        
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        addComponents(legalParametersForm, saveButton);
        
        setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
    }

    private void save() {
        legalParametersRepository.updateLegalParameters(legalParametersForm.getValue());
        logger.info("Legal parameters saved");
        Notification.show("Legal parameters updated");
    }
    
}
