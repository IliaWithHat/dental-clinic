@echo off

if "%1"=="all" (
    cd ../docker

    echo.
    echo Stopping and removing all Docker containers...
    docker-compose down --volumes=false

    cd ../bat-files

    goto :eof
)

set /p SERVICES_LIST=< services.txt

for %%s in (%SERVICES_LIST%) do (
    echo.
    echo Stopping %%s-container
    docker stop %%s-container

    echo.
    echo Removing %%s-container
    docker rm %%s-container
)

:eof