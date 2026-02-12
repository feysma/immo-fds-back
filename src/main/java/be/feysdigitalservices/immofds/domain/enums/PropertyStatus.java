package be.feysdigitalservices.immofds.domain.enums;

public enum PropertyStatus {
    DRAFT("Brouillon"),
    PUBLISHED("Publié"),
    SOLD("Vendu"),
    RENTED("Loué"),
    ARCHIVED("Archivé");

    private final String label;

    PropertyStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
