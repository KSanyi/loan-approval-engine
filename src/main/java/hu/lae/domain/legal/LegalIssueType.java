package hu.lae.domain.legal;

public enum LegalIssueType {

    BANKRUPTCY("Csődeljárás"),
    LIQUIDATION("Felszámolás"),
    DISSOLVENCY("Végelszámolás"),
    DELETION("Törlés"),
    FORCED_DELETION("Kényszertörlés"), 
    EXECUTION("Végrehajtás"),
    IDERICT("Működéstől eltiltás"),
    CONFISCATION("Vagyoni részesedés lefoglalása"),
    CRIMINAL_CHARGE("Büntetőjogi intézkedés"),
    TAX_NUMBER_SUSPENDED("Szankciós jelleggel felfüggesztett adószám"),
    TAX_NUMBER_DELETED("Szankciós jelleggel törölt adószám"),
    ASSET_SETTLEMENT_PROCEDURE("Vagyonrendezési eljárás"),
    DEBT_SETTLEMENT_PROCEDURE("Adósságrendezési eljárás"),
    TAX_EXECUTION("Adóvégrehajtás"),
    BLACK_EMPLOYER("Feketén foglaloztatók"),
    ANTICARTEL_PROCEDURE("GVH versenyhivatali eljárás"),
    CONSUMER_PROTECTION_PROCEDURE("Ffogyasztóvédelmi eljárás"),
    EMPLOYMENT_FINE("Munkaügyi elmarasztalás");
    
    public final String displayName;
    
    private LegalIssueType(String displayName) {
        this.displayName = displayName;
    }

    public static enum Level {
        GO, JUDGE, NOGO
    }
    
}
