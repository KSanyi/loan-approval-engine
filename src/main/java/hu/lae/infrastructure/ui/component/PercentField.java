package hu.lae.infrastructure.ui.component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import hu.lae.util.MathUtil;

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
    
    public BigDecimal getPercent() {
        try {
            return (BigDecimal)PF.parse(getValue());
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void setNumber(BigDecimal number) {
        setConvertedValue(number);
    }
    
    private Converter<String, BigDecimal> converter = new Converter<String, BigDecimal>() {

        {
            PF.setParseBigDecimal(true);
        }
        
        @Override
        public BigDecimal convertToModel(String value, Class<? extends BigDecimal> targetType, Locale locale) throws ConversionException {
            try {
                if(!value.contains("%")) {
                    return MathUtil.div(new BigDecimal(value).setScale(2), new BigDecimal("100"));
                }
                return (BigDecimal)PF.parse(value);
            } catch (Exception e) {
                throw new ConversionException("Can not parse to percent");
            }
        }

        @Override
        public String convertToPresentation(BigDecimal value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            return PF.format(value);
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
