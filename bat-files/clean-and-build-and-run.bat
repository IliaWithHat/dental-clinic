@echo off

if "%1"=="all" (
    call ./clean.bat all
    call ./build-and-run.bat
) else (
    call ./clean.bat
    call ./build-and-run.bat
)