@echo off
cd /d "e:\project\self\demo\book-catalog-service"
set JAVA_HOME=E:\works\jdk\jdk17
call .\mvnw spring-boot:run -e > mvn-output.log 2>&1
type mvn-output.log
pause