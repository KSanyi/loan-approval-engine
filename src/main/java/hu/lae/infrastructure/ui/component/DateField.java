package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DateField extends com.vaadin.ui.DateField {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public DateField(String caption) {
        super(caption);
        addValueChangeListener(e -> logger.debug(caption + " is set to " + e.getValue()));
    }
    
}
