@echo off
title Run APU Medical Centre HMS
echo ========================================================
echo   Compiling & Launching APU Medical Centre HMS...
echo ========================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin -sourcepath src src/model/*.java src/data/*.java src/gui/*.java src/main/Main.java src/test/*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please check your Java code.
    pause
    exit /b %ERRORLEVEL%
)

java -cp bin main.Main
