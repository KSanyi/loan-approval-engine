package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class CheckBox extends com.vaadin.ui.CheckBox {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public CheckBox(String caption) {
       this(caption, caption);
    }
    
    public CheckBox(String caption, String id) {
        setCaption(caption);
        setId(id);
        addValueChangeListener(e -> {
            if(e.isUserOriginated()) {
                logger.debug("USERACTION {}: '{}' {}", LaeUI.currentUser(), getId(), e.getValue() ? "checked" : "unchecked");
            }
        });
     }
    
}
