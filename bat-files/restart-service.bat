@echo off

if "%1"=="" (
    echo No service name provided. Usage: restart-service.bat ^<service-name^>
    goto :eof
)

set SERVICES_LIST=%*
set DOCKERHUB_USERNAME=iliawithhat

for %%s in (%SERVICES_LIST%) do (
    echo.
    echo Stopping and removing %%s from Docker...
    docker stop %%s-container
    docker rm %%s-container
    docker rmi %DOCKERHUB_USERNAME%/%%s
)

call build-and-push.bat %*
call run.bat %*

:eof