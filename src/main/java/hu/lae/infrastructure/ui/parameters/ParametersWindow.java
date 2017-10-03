package hu.lae.infrastructure.ui.parameters;

import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.industry.IndustryDataRepository;
import hu.lae.domain.legal.LegalParametersRepository;
import hu.lae.domain.riskparameters.RiskParametersRepository;
import hu.lae.infrastructure.ui.parameters.industrydata.IndustryDataScreen;
import hu.lae.infrastructure.ui.parameters.legalparameters.LegalParametersScreen;
import hu.lae.infrastructure.ui.parameters.riskparameters.RiskParametersScreen;

@SuppressWarnings("serial")
public class ParametersWindow extends Window {

    private final TabSheet tabsheet = new TabSheet();

    private ParametersWindow(RiskParametersRepository riskParameterRepository, LegalParametersRepository legalParametersRepository, IndustryDataRepository industryDataRepository) {
        
        setCaption("Parameters");
        
        tabsheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabsheet.setSizeFull();
        
        Tab tab = tabsheet.addTab(new RiskParametersScreen(riskParameterRepository));
        tab.setCaption("Risk parameters");
        
        Tab tab2 = tabsheet.addTab(new LegalParametersScreen(legalParametersRepository));
        tab2.setCaption("Legal parameters");

        Tab tab3 = tabsheet.addTab(new IndustryDataScreen(industryDataRepository));
        tab3.setCaption("Industry parameters");
        
        Tab tab4 = tabsheet.addTab(new Label("Under construction"));
        tab4.setCaption("Other parameters");
        
        setContent(tabsheet);
        setModal(true);
        setWidth("1300px");
        setHeight("750px");
    }
    
    public static void show(RiskParametersRepository riskParameterRepository, LegalParametersRepository legalParametersRepository, IndustryDataRepository industryDataRepository) {
        UI.getCurrent().addWindow(new ParametersWindow(riskParameterRepository, legalParametersRepository, industryDataRepository));
    }
    
}
