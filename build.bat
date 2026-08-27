@echo off
title Build APU Medical Centre HMS
echo ========================================================
echo   APU Medical Centre Hospital Management System (HMS)
echo   Compiling Java Source Code...
echo ========================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin -sourcepath src src/model/*.java src/data/*.java src/gui/*.java src/main/Main.java src/test/*.java

if %ERRORLEVEL% EQU 0 (
    echo [BUILD SUCCESS] All classes compiled successfully into bin/
) else (
    echo [BUILD FAILED] Compilation errors occurred.
)
pause

