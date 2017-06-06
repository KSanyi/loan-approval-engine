package hu.lae.domain.validation;

public class ValidationResult {

    public static final ValidationResult Ok() {
        return new ValidationResult(Type.OK, null);
    }
    
    public static final ValidationResult Ko(String message) {
        return new ValidationResult(Type.KO, message);
    }
    
    public static final ValidationResult Warning(String message) {
        return new ValidationResult(Type.WARNING, message);
    }
    
    public final Type type;
    
    private final String message;
    
    private ValidationResult(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public static enum Type {
        OK, WARNING, KO;
    }
    
    @Override
    public String toString() {
        return type.name() + " " + (message == null ? "" : ": " + message);
    }
    
}
