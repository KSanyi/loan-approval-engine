package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Button extends com.vaadin.ui.Button {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public Button(String caption) {
        super(caption);
        addClickListener(click -> logger.debug("Button '" + caption + "' clicked"));
    }
    
    public Button(String caption, ClickListener listener) {
        super(caption, listener);
        addClickListener(click -> logger.debug("Button '" + caption + "' clicked"));
    }
    
}
