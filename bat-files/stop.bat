@echo off

echo.
echo Stopping Docker containers...
docker stop postgres-container zookeeper-container kafka-container keycloak-container config-server-container eureka-server-container gateway-server-container user-service-container time-service-container appointment-service-container review-service-container mail-sender-service-container mail-scheduler-service-container

echo.
echo Removing Docker containers...
docker rm postgres-container zookeeper-container kafka-container keycloak-container config-server-container eureka-server-container gateway-server-container user-service-container time-service-container appointment-service-container review-service-container mail-sender-service-container mail-scheduler-service-container