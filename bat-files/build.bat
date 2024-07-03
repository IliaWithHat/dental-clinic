@echo off
cd ..

set SERVICES=config-server eureka-server gateway-server user-service time-service appointment-service review-service mail-sender-service mail-scheduler-service

for %%s in (%SERVICES%) do (
    echo.
    echo Building %%s...
    cd %%s
    call ./gradlew.bat bootJar
    cd ..
)

cd bat-files