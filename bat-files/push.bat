@echo off
cd ..

set DOCKERHUB_USERNAME=iliawithhat
set SERVICES=config-server eureka-server gateway-server user-service time-service appointment-service review-service mail-sender-service mail-scheduler-service

for %%s in (%SERVICES%) do (
    cd %%s

    echo.
    echo Building image
    docker build -t %%s .

    echo.
    echo Setting tag to image
    docker tag %%s:latest %DOCKERHUB_USERNAME%/%%s:latest

    echo.
    echo Pushing image to Docker Hub
    docker push %DOCKERHUB_USERNAME%/%%s:latest

    echo.
    echo Removing local image %%s
    docker rmi %%s

    echo.
    echo Image for %%s pushed to Docker Hub successfully.

    cd ..
)

cd bat-files