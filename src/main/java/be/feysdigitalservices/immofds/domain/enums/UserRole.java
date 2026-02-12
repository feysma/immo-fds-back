package be.feysdigitalservices.immofds.domain.enums;

public enum UserRole {
    ADMIN("Administrateur"),
    SUPER_ADMIN("Super administrateur");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
