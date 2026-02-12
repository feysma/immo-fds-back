CREATE TABLE contact_requests (
    id                  BIGSERIAL PRIMARY KEY,
    contact_type        VARCHAR(20) NOT NULL,
    status              VARCHAR(15) NOT NULL DEFAULT 'NEW',
    first_name          VARCHAR(255) NOT NULL,
    last_name           VARCHAR(255) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    phone               VARCHAR(50),
    message             TEXT,
    property_reference  VARCHAR(20),
    property_address    VARCHAR(500),
    property_type       VARCHAR(30),
    estimated_price     NUMERIC(12, 2),
    admin_notes         TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contact_requests_status ON contact_requests (status);
CREATE INDEX idx_contact_requests_contact_type ON contact_requests (contact_type);
CREATE INDEX idx_contact_requests_created_at ON contact_requests (created_at);
