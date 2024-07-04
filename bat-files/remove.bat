@echo off

set /p SERVICES_LIST=< services.txt
set DOCKERHUB_USERNAME=iliawithhat

for %%s in (%SERVICES_LIST%) do (
    echo.
    echo Removing %%s
    docker rmi %DOCKERHUB_USERNAME%/%%s
)