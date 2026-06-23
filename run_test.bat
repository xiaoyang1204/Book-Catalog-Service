@echo off
set "JAVA_HOME=C:\Users\xingchongyang\.jdks\graalvm-jdk-17.0.12"
cd /d e:\project\self\demo\book-catalog-service
mvnw.cmd clean test
pause