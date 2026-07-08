@REM Maven Wrapper script for Windows
@REM Auto-downloads Maven if not installed
@REM Usage: mvnw.cmd clean install

@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

@REM Read distributionUrl from properties file
for /f "usebackq tokens=1,2 delims==" %%a in ("%MAVEN_WRAPPER_PROPERTIES%") do (
    if "%%a"=="distributionUrl" set DOWNLOAD_URL=%%b
)

set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6
set MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd

@REM Check if Maven is already downloaded
if exist "%MVN_CMD%" goto runMvn

@REM Download Maven
echo Maven not found. Downloading Maven 3.9.6...
mkdir "%MAVEN_HOME%" 2>nul
set MAVEN_ZIP=%TEMP%\apache-maven-3.9.6-bin.zip

powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%MAVEN_ZIP%'}"

echo Extracting Maven...
powershell -Command "& {Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force}"

@REM Maven extracts to a nested folder, move contents up
if exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6\bin\mvn.cmd" goto runMvn

@REM Handle nested extraction
for /d %%i in ("%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6\*") do (
    if exist "%%i\bin\mvn.cmd" (
        xcopy /s /e /y "%%i\*" "%MAVEN_HOME%\" >nul
        rd /s /q "%%i" 2>nul
    )
)

del "%MAVEN_ZIP%" 2>nul

:runMvn
"%MVN_CMD%" %*
