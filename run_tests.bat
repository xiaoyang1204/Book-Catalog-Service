@echo off
set JAVA_HOME=C:\ProgramData\Oracle\Java\javapath
cd /d e:\project\self\demo\book-catalog-service
mvnw.cmd clean test
pause