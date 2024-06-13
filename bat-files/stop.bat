@echo off

echo.
echo Stopping Docker containers...
docker stop postgres-container keycloak-container config-server-container eureka-server-container user-service-container time-service-container appointment-service-container mail-sender-service-container

echo.
echo Removing Docker containers...
docker rm postgres-container keycloak-container config-server-container eureka-server-container user-service-container time-service-container appointment-service-container mail-sender-service-container