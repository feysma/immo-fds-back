package be.feysdigitalservices.immofds.domain.enums;

public enum ContactStatus {
    NEW("Nouveau"),
    IN_PROGRESS("En cours"),
    CLOSED("Clôturé");

    private final String label;

    ContactStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
