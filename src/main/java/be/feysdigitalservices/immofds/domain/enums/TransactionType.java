package be.feysdigitalservices.immofds.domain.enums;

public enum TransactionType {
    SALE("Vente"),
    RENT("Location");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
