package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Resource;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class Button extends com.vaadin.ui.Button {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Button(String caption, String id) {
        super(caption);
        setId(id);
        addClickListener(click -> logger.debug("USERACTION {}: '{}' clicked", LaeUI.currentUser(), id));
    }
    
    public Button(String caption) {
        this(caption, caption);
    }
    
    public Button(String caption, ClickListener listener) {
        this(caption);
        addClickListener(listener);
    }
    
    public Button(Resource resource, String id) {
        super(resource);
        setId(id);
        addClickListener(click -> logger.debug("USERACTION {}: '{}' clicked", LaeUI.currentUser(), id));
    }
    
}
