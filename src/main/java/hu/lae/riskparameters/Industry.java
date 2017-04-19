package hu.lae.riskparameters;

public enum Industry {

    CONSTRUCTION("Construction"),
    
    AUTOMOTIVE("Automotive");
    
    public final String displayName;

    private Industry(String displayName) {
        this.displayName = displayName;
    }
    
}
