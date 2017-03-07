package hu.lae.infrastructure.ui.component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class PercentField extends TextField {

    private final DecimalFormat PF = new DecimalFormat("0.0%");
    
    public PercentField(String caption) {
        super(caption);
        setWidth("60px");
        addStyleName(ValoTheme.TEXTFIELD_SMALL);
        addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
        setConverter(converter);
        setImmediate(true);
    }
    
    public double getPercent() {
        try {
            return (Double)PF.parse(getValue());
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void setNumber(Double number) {
        setConvertedValue(number);
    }
    
    private Converter<String, Double> converter = new Converter<String, Double>() {

        @Override
        public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
            try {
                if(!value.contains("%")) {
                    return Double.parseDouble(value) / 100;
                }
                return (Double)PF.parse(value);
            } catch (Exception ex) {
                throw new ConversionException("Can not parse to percent");
            }
        }

        @Override
        public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return PF.format(value);
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
