CREATE TABLE properties (
    id              BIGSERIAL PRIMARY KEY,
    reference       VARCHAR(20) NOT NULL UNIQUE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    property_type   VARCHAR(30) NOT NULL,
    transaction_type VARCHAR(10) NOT NULL,
    status          VARCHAR(15) NOT NULL DEFAULT 'DRAFT',
    price           NUMERIC(12, 2) NOT NULL,
    surface         DOUBLE PRECISION,
    bedrooms        INTEGER,
    bathrooms       INTEGER,
    rooms           INTEGER,
    floors          INTEGER,
    construction_year INTEGER,
    energy_rating   VARCHAR(15),
    garden          BOOLEAN NOT NULL DEFAULT FALSE,
    garage          BOOLEAN NOT NULL DEFAULT FALSE,
    terrace         BOOLEAN NOT NULL DEFAULT FALSE,
    basement        BOOLEAN NOT NULL DEFAULT FALSE,
    elevator        BOOLEAN NOT NULL DEFAULT FALSE,
    furnished       BOOLEAN NOT NULL DEFAULT FALSE,
    street          VARCHAR(255) NOT NULL,
    number          VARCHAR(10),
    postal_code     VARCHAR(10) NOT NULL,
    city            VARCHAR(255) NOT NULL,
    province        VARCHAR(25) NOT NULL,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_properties_reference ON properties (reference);
CREATE INDEX idx_properties_status ON properties (status);
CREATE INDEX idx_properties_property_type ON properties (property_type);
CREATE INDEX idx_properties_transaction_type ON properties (transaction_type);
CREATE INDEX idx_properties_province ON properties (province);
CREATE INDEX idx_properties_city ON properties (city);
CREATE INDEX idx_properties_price ON properties (price);
CREATE INDEX idx_properties_postal_code ON properties (postal_code);
