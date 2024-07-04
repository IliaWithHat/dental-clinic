@echo off

if "%1"=="" (
    set /p SERVICES_LIST=< services.txt
) else (
    set SERVICES_LIST=%*
)

cd ..

for %%s in (%SERVICES_LIST%) do (
    echo.
    echo Building %%s...
    cd %%s
    call ./gradlew.bat bootJar
    cd ..
)

cd bat-files