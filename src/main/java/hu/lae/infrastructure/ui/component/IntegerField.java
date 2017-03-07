package hu.lae.infrastructure.ui.component;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class IntegerField extends TextField {

    public IntegerField(String caption) {
        super(caption);
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        setConverter(converter);
    }
    
    public Integer getNumber() {
        return Integer.parseInt(getValue());
    }
    
    public void setNumber(Integer number) {
        setConvertedValue(number);
    }
    
    private Converter<String, Integer> converter = new Converter<String, Integer>() {

        @Override
        public Integer convertToModel(String value, Class<? extends Integer> targetType, Locale locale) throws ConversionException {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public String convertToPresentation(Integer value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return value.toString();
        }

        @Override
        public Class<Integer> getModelType() {
            return Integer.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    };
    
}
