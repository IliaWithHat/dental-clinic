@echo off

echo.
echo Removing Docker images...
docker rmi config-server-image eureka-server-image gateway-server-image user-service-image time-service-image appointment-service-image review-service-image mail-sender-service-image mail-scheduler-service-image