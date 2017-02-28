package hu.lae.infrastructure.ui.component;

import java.math.BigDecimal;
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
    
    public BigDecimal getNumber() {
        return new BigDecimal(getValue());
    }
    
    public void setNumber(BigDecimal number) {
        setConvertedValue(number);
    }
    
    private Converter<String, BigDecimal> converter = new Converter<String, BigDecimal>() {

        @Override
        public BigDecimal convertToModel(String value, Class<? extends BigDecimal> targetType, Locale locale) throws ConversionException {
            try {
                return new BigDecimal(value);
            } catch (Exception e) {
                throw new ConversionException("Can not parse to number");
            }
        }

        @Override
        public String convertToPresentation(BigDecimal value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return value.toString();
        }

        @Override
        public Class<BigDecimal> getModelType() {
            return BigDecimal.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    };
    
}
