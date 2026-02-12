package be.feysdigitalservices.immofds.domain.enums;

public enum Province {
    BRUXELLES_CAPITALE("Bruxelles-Capitale"),
    BRABANT_WALLON("Brabant wallon"),
    BRABANT_FLAMAND("Brabant flamand"),
    ANVERS("Anvers"),
    LIMBOURG("Limbourg"),
    LIEGE("Liège"),
    NAMUR("Namur"),
    HAINAUT("Hainaut"),
    LUXEMBOURG("Luxembourg"),
    FLANDRE_OCCIDENTALE("Flandre occidentale"),
    FLANDRE_ORIENTALE("Flandre orientale");

    private final String label;

    Province(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
