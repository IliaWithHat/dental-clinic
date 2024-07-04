@echo off

if "%1"=="all" (
    call ./stop.bat %1
) else (
    call ./stop.bat
)
call ./remove.bat