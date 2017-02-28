package hu.lae.infrastructure.ui.tmp;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class StartWindow extends Window {

    public StartWindow() {
        setCaption("");
        setModal(true);
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
        
        HorizontalLayout stLayout = new HorizontalLayout();
        stLayout.setSpacing(true);
        
        HorizontalLayout ltLayout = new HorizontalLayout();
        ltLayout.setSpacing(true);
        
        Label stLabel = new Label("");
        stLabel.setWidth("150px");
        
        Label ltLabel = new Label("");
        ltLabel.setWidth("150px");
        
        Slider shortLoanSlider = new Slider();
        //shortLoanSlider.setCaption("<b>This is it</b>");
        shortLoanSlider.setCaptionAsHtml(true);
        shortLoanSlider.setDescription("This is a short term loan xxx");
        shortLoanSlider.setOrientation(SliderOrientation.HORIZONTAL);
        shortLoanSlider.setMax(200);
        shortLoanSlider.setWidth("500px");
        shortLoanSlider.setValue(100d);
        stLayout.addComponents(new Label("S/T loan"), shortLoanSlider, stLabel);
        
        Slider longLoanSlider = new Slider();
        longLoanSlider.setOrientation(SliderOrientation.HORIZONTAL);
        longLoanSlider.setMax(500);
        longLoanSlider.setValue(230d);
        longLoanSlider.setWidth("500px");
        //longLoanSlider.setReadOnly(true);
        longLoanSlider.setEnabled(false);
        ltLayout.addComponents(new Label("L/T loan"), longLoanSlider, ltLabel);

        layout.addComponents(stLayout, ltLayout);
        
        shortLoanSlider.addValueChangeListener(v -> {
            //longLoanSlider.setReadOnly(false);
            double value = 100 - (double)v.getProperty().getValue() + 250d;
            longLoanSlider.setValue(value);
            stLabel.setValue((double)v.getProperty().getValue() + " million Ft");
            ltLabel.setValue("950.0 million Ft");
            //longLoanSlider.setReadOnly(true);
            });
        shortLoanSlider.setImmediate(true);
    }
    
}
