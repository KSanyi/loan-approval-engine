package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class DateField extends com.vaadin.ui.DateField {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public DateField(String caption) {
        this(caption, caption);
    }
    
    public DateField(String caption, String name) {
        super(caption);
        addValueChangeListener(e -> {
          if(e.isUserOriginated()) {
              logger.debug("USERACTION {}: {} is set to {}", LaeUI.currentUser(), name, e.getValue());
          }
        });
    }
    
}
