@echo off
cd ..

echo.
echo Building config-server...
cd config-server
call ./gradlew.bat bootJar
cd ..

echo.
echo Building eureka-server...
cd eureka-server
call ./gradlew.bat bootJar
cd ..

echo.
echo Building gateway-server...
cd gateway-server
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

echo.
echo Building review-service...
cd review-service
call ./gradlew.bat bootJar
cd ..

echo.
echo Building mail-sender-service...
cd mail-sender-service
call ./gradlew.bat bootJar
cd ..

echo.
echo Building mail-scheduler-service...
cd mail-scheduler-service
call ./gradlew.bat bootJar
cd ..

cd bat-files