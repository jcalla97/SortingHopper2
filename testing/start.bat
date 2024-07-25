cd ..\
call gradlew build
cd testing
xcopy /s/y ..\build\libs\sortinghopper2-2.7.0-1.21.jar .\plugins\.

wait 1

java -jar server.jar --nogui