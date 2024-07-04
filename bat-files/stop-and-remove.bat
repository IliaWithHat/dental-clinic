@echo off

if "%1"=="all" (
    call ./stop.bat all
    call ./remove.bat
) else (
    call ./stop.bat
    call ./remove.bat
)