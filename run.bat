@echo off
echo ===========================================
echo  Compiling Task and Productivity Manager...
echo ===========================================
if not exist "bin" mkdir bin
javac -cp "lib/*" src/*.java -d bin
if %ERRORLEVEL% NEQ 0 (
    echo Compilation Failed!
    pause
    exit /b %ERRORLEVEL%
)
echo Running App...
java -cp "lib/*;bin" Main
