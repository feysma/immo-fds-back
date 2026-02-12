-- Default super admin user
-- Email: admin@immofds.be / Password: Admin@2026!
-- BCrypt hash generated for: Admin@2026!
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES (
    'admin@immofds.be',
    '$2a$10$6JjZ0eIBfI74rrVhZN2.6.bdSpr4nb8LTymUvuiShr7.gzeVdyKBi',
    'Admin',
    'ImmoFDS',
    'SUPER_ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
