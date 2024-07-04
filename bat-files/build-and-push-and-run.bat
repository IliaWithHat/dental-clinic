@echo off

if "%1"=="" (
    call ./build.bat
    call ./push.bat
) else (
    call ./build.bat %*
    call ./push.bat %*
)
call ./run.bat