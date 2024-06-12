@echo off

echo.
echo Removing Docker images...
docker rmi config-server-image eureka-server-image user-service-image time-service-image appointment-service-image mail-service-image