package be.feysdigitalservices.immofds.domain.enums;

public enum PropertyType {
    HOUSE("Maison"),
    APARTMENT("Appartement"),
    STUDIO("Studio"),
    LOFT("Loft"),
    OFFICE("Bureau"),
    RETAIL_SPACE("Commerce"),
    WAREHOUSE("Entrepôt"),
    LAND("Terrain"),
    GARAGE("Garage"),
    PARKING_SPOT("Emplacement de parking");

    private final String label;

    PropertyType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
