package be.feysdigitalservices.immofds.domain.enums;

public enum ContactType {
    SELL_YOUR_HOME("Vendre votre bien"),
    GENERAL_CONTACT("Contact général"),
    VISIT_REQUEST("Demande de visite");

    private final String label;

    ContactType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
