@echo off

echo.
echo Stopping Docker containers...
docker stop postgres-container keycloak-container config-server-container user-service-container

echo.
echo Removing Docker containers...
docker rm postgres-container keycloak-container config-server-container user-service-container