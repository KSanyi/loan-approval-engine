package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class RadioButtonGroup<T> extends com.vaadin.ui.RadioButtonGroup<T> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public RadioButtonGroup(String caption, Collection<T> items) {
        super(caption, items);
        setId(caption);
        addValueChangeListener(e -> {
            if(e.isUserOriginated()) {
                logger.debug("USERACTION {}: '{}' {} selected", LaeUI.currentUser(), getId(), e.getValue());
            }
        });
     }
    
}
