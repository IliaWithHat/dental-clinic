@echo off

if "%1"=="all" (
    echo.
    echo Stopping all Docker containers...
    docker stop postgres-container zookeeper-container kafka-container keycloak-container config-server-container eureka-server-container gateway-server-container user-service-container time-service-container appointment-service-container review-service-container mail-sender-service-container mail-scheduler-service-container

    echo.
    echo Removing all Docker containers...
    docker rm postgres-container zookeeper-container kafka-container keycloak-container config-server-container eureka-server-container gateway-server-container user-service-container time-service-container appointment-service-container review-service-container mail-sender-service-container mail-scheduler-service-container
) else (
    echo.
    echo Stopping specific Docker containers...
    docker stop config-server-container eureka-server-container gateway-server-container user-service-container time-service-container appointment-service-container review-service-container mail-sender-service-container mail-scheduler-service-container

    echo.
    echo Removing specific Docker containers...
    docker rm config-server-container eureka-server-container gateway-server-container user-service-container time-service-container appointment-service-container review-service-container mail-sender-service-container mail-scheduler-service-container
)