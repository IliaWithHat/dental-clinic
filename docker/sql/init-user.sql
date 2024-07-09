\c keycloak
CREATE USER keycloak_user WITH PASSWORD 'password';

GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO keycloak_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloak_user;

\c dental_clinic_repository
CREATE USER appointment_user WITH PASSWORD 'password';
CREATE USER review_user WITH PASSWORD 'password';
CREATE USER time_user WITH PASSWORD 'password';

GRANT USAGE ON SCHEMA appointment_service_schema TO appointment_user;
GRANT ALL PRIVILEGES ON SCHEMA appointment_service_schema TO appointment_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA appointment_service_schema TO appointment_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA appointment_service_schema GRANT ALL ON TABLES TO appointment_user;

GRANT USAGE ON SCHEMA review_service_schema TO review_user;
GRANT ALL PRIVILEGES ON SCHEMA review_service_schema TO review_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA review_service_schema TO review_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA review_service_schema GRANT ALL ON TABLES TO review_user;

GRANT USAGE ON SCHEMA time_service_schema TO time_user;
GRANT ALL PRIVILEGES ON SCHEMA time_service_schema TO time_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA time_service_schema TO time_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA time_service_schema GRANT ALL ON TABLES TO time_user;