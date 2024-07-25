cd ..\
call gradlew build
cd testing
xcopy /s/y ..\build\libs\sortinghopper2-2.7.0-1.21.jar .\plugins\.

timeout /t 1 /nobreak > NUL

java -jar server.jar --nogui