@echo off

if "%1"=="" (
    set /p SERVICES_LIST=< services.txt
) else (
    set SERVICES_LIST=%*
)

cd ..

set DOCKERHUB_USERNAME=iliawithhat

for %%s in (%SERVICES_LIST%) do (
    cd %%s

    echo.
    echo Building image %%s
    docker build -t %%s .

    echo.
    echo Setting tag to image %%s
    docker tag %%s:latest %DOCKERHUB_USERNAME%/%%s:latest

    echo.
    echo Pushing image %%s to Docker Hub
    docker push %DOCKERHUB_USERNAME%/%%s:latest

    echo.
    echo Removing local image %%s
    docker rmi %%s

    echo.
    echo Image for %%s pushed to Docker Hub successfully.

    cd ..
)

cd bat-files