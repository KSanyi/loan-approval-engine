package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ComboBox<T> extends com.vaadin.ui.ComboBox<T> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public ComboBox(String caption, Collection<T> options) {
        this(caption, caption, options);
    }
    
    public ComboBox(String caption, String id, Collection<T> options) {
        super(caption, options);
        addValueChangeListener(e -> {
            if(e.isUserOriginated()) {
                logger.debug("USERACTION: "+ id + ": '" + e.getValue() + "' selected");
            }
        });
    }
    
}
