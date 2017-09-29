package hu.lae.infrastructure.ui.riskparameters;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.riskparameters.RiskParameterRepository;
import hu.lae.infrastructure.ui.component.Button;

@SuppressWarnings("serial")
public class ParametersWindow extends Window {

    private final RiskParameterRepository riskParameterRepository;
    
    private final Button saveButton = new Button("Save", click -> save());
    
    private final RiskParametersForm riskParametersForm;
    
    private final TabSheet tabsheet = new TabSheet();

    public ParametersWindow(RiskParameterRepository riskParameterRepository) {
        
        setCaption("Parameters");
        
        this.riskParameterRepository = riskParameterRepository;
        
        this.riskParametersForm = new RiskParametersForm(riskParameterRepository.loadRiskParameters());
        VerticalLayout layout = new VerticalLayout(riskParametersForm, saveButton);
        
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        
        layout.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        
        tabsheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabsheet.setSizeFull();
        
        Tab tab = tabsheet.addTab(layout);
        tab.setCaption("Risk parameters");
        
        Tab tab2 = tabsheet.addTab(new Label("Industry parameters"));
        tab2.setCaption("Industry parameters");
        
        Tab tab3 = tabsheet.addTab(new Label("Legal parameters"));
        tab3.setCaption("Legal parameters");
        
        Tab tab4 = tabsheet.addTab(new Label("Other parameters"));
        tab4.setCaption("Other parameters");
        
        setContent(tabsheet);
        setModal(true);
        setWidth("1300px");
        setHeight("450px");
    }
    
    private void save() {
        riskParameterRepository.updateRiskParameters(riskParametersForm.getRiskParameters());
        Notification.show("Risk parameters updated");
    }

    public static void show(RiskParameterRepository riskParameterRepository) {
        UI.getCurrent().addWindow(new ParametersWindow(riskParameterRepository));
    }
    
}
