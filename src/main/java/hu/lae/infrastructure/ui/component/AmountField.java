package hu.lae.infrastructure.ui.component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class AmountField extends TextField {

    private final DecimalFormat FORMAT = new DecimalFormat("###,###");
    
    public AmountField(String caption) {
        super(caption);
        setWidth("90px");
        setMaxLength(8);
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        setConverter(converter);
        setImmediate(true);
    }
    
    public Long getAmount() {
        try {
            return (Long)FORMAT.parse(getValue());
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void setAmount(Long amount) {
        setConvertedValue(amount);
    }
    
    private Converter<String, Long> converter = new Converter<String, Long>() {

        @Override
        public Long convertToModel(String value, Class<? extends Long> targetType, Locale locale) throws ConversionException {
            try {
                String cleanedValue = value != null ? value.replaceAll("[^0-9]","") : "";
                return cleanedValue.isEmpty() ? 0 : new Long(cleanedValue);
            } catch (Exception ex) {
                throw new ConversionException("Can not parse to number: " + value + " " + ex.getMessage());
            }
        }

        @Override
        public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return FORMAT.format(value);
        }

        @Override
        public Class<Long> getModelType() {
            return Long.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    };
    
}
