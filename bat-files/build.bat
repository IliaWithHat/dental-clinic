@echo off
cd ..

echo.
echo Building config-server...
cd config-server
call ./gradlew.bat bootJar
cd ..

echo.
echo Building user-service...
cd user-service
call ./gradlew.bat bootJar
cd ..

echo.
echo Building time-service...
cd time-service
call ./gradlew.bat bootJar
cd ..

echo.
echo Building appointment-service...
cd appointment-service
call ./gradlew.bat bootJar
cd ..

cd bat-files