package hu.lae.domain.legal;

public enum LegalIssueType {

    BANKRUPTCY("csődeljárás"),
    LIQUIDATION("felszámolás"),
    DISSOLVENCY("végelszámolás"),
    DELETION("törlés"),
    FORCED_DELETION("kényszertörlés"), 
    EXECUTION("végrehajtás"),
    IDERICT("működéstől eltiltás"),
    CONFISCATION("vagyoni részesedés lefoglalása"),
    CRIMINAL_CHARGE("büntetőjogi intézkedés"),
    TAX_NUMBER_SUSPENDED("szankciós jelleggel felfüggesztett adószám"),
    TAX_NUMBER_DELETED("szankciós jelleggel törölt adószám"),
    ASSET_SETTLEMENT_PROCEDURE("vagyonrendezési eljárás"),
    DEBT_SETTLEMENT_PROCEDURE("adósságrendezési eljárás"),
    TAX_EXECUTION("adóvégrehajtás"),
    BLACK_EMPLOYER("feketén foglaloztatók"),
    ANTICARTEL_PROCEDURE("GVH versenyhivatali eljárás"),
    CONSUMER_PROTECTION_PROCEDURE("fogyasztóvédelmi eljárás"),
    EMPLOYMENT_FINE("munkaügyi elmarasztalás");
    
    public final String displayName;
    
    private LegalIssueType(String displayName) {
        this.displayName = displayName;
    }

    public static enum Level {
        GO, JUDGE, NOGO
    }
    
}
