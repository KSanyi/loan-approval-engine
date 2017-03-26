package hu.lae.infrastructure.ui.component;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class NumberField extends TextField {

    public NumberField(String caption) {
        super(caption);
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        setConverter(converter);
    }
    
    public Double getNumber() {
        return Double.parseDouble(getValue());
    }
    
    public void setNumber(Double number) {
        setConvertedValue(number);
    }
    
    private Converter<String, Double> converter = new Converter<String, Double>() {

        @Override
        public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
            try {
                return Double.parseDouble(value);
            } catch (Exception e) {
                throw new ConversionException("Can not parse to number");
            }
        }

        @Override
        public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return value.toString();
        }

        @Override
        public Class<Double> getModelType() {
            return Double.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    };
    
}
