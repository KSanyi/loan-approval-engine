package hu.lae.infrastructure.ui.loancalculation;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
class CalculationsWindow extends Window {

    CalculationsWindow() {
        setModal(true);
        setPosition(0, 0);
        setCaption("Calculations");
        TextArea textArea = new TextArea();
        textArea.setValue("");
        textArea.setRows(17);
        setContent(textArea);
    }
    
}
