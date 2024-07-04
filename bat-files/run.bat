@echo off
cd ..

echo.
echo Starting docker containers...
cd docker

if "%1"=="" (
    docker-compose up -d
) else (
    docker-compose up -d %*
)

cd ..

cd bat-files