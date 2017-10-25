package hu.lae.domain.legal;

public enum LegalIssueType {

    BANKRUPTCY("Csődeljárás", false),
    LIQUIDATION("Felszámolás", false),
    DISSOLVENCY("Végelszámolás", false),
    DELETION("Törlés", false),
    FORCED_DELETION("Kényszertörlés", false), 
    EXECUTION("Végrehajtás", true),
    IDERICT("Működéstől eltiltás", false),
    CONFISCATION("Vagyoni részesedés lefoglalása", true),
    CRIMINAL_CHARGE("Büntetőjogi intézkedés", false),
    TAX_NUMBER_SUSPENDED("Szankciós jelleggel felfüggesztett adószám", false),
    TAX_NUMBER_DELETED("Szankciós jelleggel törölt adószám", false),
    ASSET_SETTLEMENT_PROCEDURE("Vagyonrendezési eljárás", false),
    DEBT_SETTLEMENT_PROCEDURE("Adósságrendezési eljárás", false),
    TAX_EXECUTION("Adóvégrehajtás", true),
    BLACK_EMPLOYER("Feketén foglaloztatók", false),
    ANTICARTEL_PROCEDURE("GVH versenyhivatali eljárás", false),
    CONSUMER_PROTECTION_PROCEDURE("Fogyasztóvédelmi eljárás", true),
    EMPLOYMENT_FINE("Munkaügyi elmarasztalás", true);
    
    public final String displayName;
    
    public final boolean hasMaterialityThreshold;
    
    private LegalIssueType(String displayName, boolean hasMaterialityThreshold) {
        this.displayName = displayName;
        this.hasMaterialityThreshold = hasMaterialityThreshold;
    }

    public static enum Level {
        GO, JUDGE, NOGO
    }
    
}
