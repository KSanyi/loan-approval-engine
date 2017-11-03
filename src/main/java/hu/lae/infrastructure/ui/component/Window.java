package hu.lae.infrastructure.ui.component;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.infrastructure.ui.LaeUI;

@SuppressWarnings("serial")
public class Window extends com.vaadin.ui.Window {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    @Override
    public void close() {
        logger.debug("USERACTION {}: '{}' is closed", LaeUI.currentUser(), getClass().getSimpleName());
        super.close();
    }
    
}
