@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM AgriBridge Maven Wrapper - Windows
REM Maven is downloaded to the user's local .m2 wrapper directory.
REM No system-wide Maven installation is required.

set "BASE_DIR=%~dp0"
set "PROPS=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties"

if not exist "%PROPS%" (
  echo ERROR: Maven wrapper properties not found:
  echo %PROPS%
  exit /b 1
)

for /f "usebackq tokens=1,* delims==" %%A in ("%PROPS%") do (
  if "%%A"=="distributionUrl" set "DIST_URL=%%B"
)

if not defined DIST_URL (
  echo ERROR: distributionUrl is missing from maven-wrapper.properties
  exit /b 1
)

set "MAVEN_VERSION=3.9.16"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"

if exist "%MAVEN_HOME%\bin\mvn.cmd" goto run_maven

echo.
echo Maven %MAVEN_VERSION% is not installed for this project.
echo Downloading Maven automatically. This is a one-time download.
echo.

set "TMP_ZIP=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%TMP_ZIP%'"

if errorlevel 1 (
  echo ERROR: Could not download Maven.
  echo Check your internet connection and try again.
  exit /b 1
)

if exist "%MAVEN_HOME%" rmdir /s /q "%MAVEN_HOME%"
mkdir "%MAVEN_HOME%" >nul 2>&1

set "TMP_EXTRACT=%TEMP%\agribridge-maven-extract-%RANDOM%"
mkdir "%TMP_EXTRACT%" >nul 2>&1

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "Expand-Archive -LiteralPath '%TMP_ZIP%' -DestinationPath '%TMP_EXTRACT%' -Force"

if errorlevel 1 (
  echo ERROR: Could not extract Maven.
  exit /b 1
)

move "%TMP_EXTRACT%\apache-maven-%MAVEN_VERSION%" "%MAVEN_HOME%" >nul

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo ERROR: Maven executable was not found after extraction.
  exit /b 1
)

del /q "%TMP_ZIP%" >nul 2>&1
rmdir /s /q "%TMP_EXTRACT%" >nul 2>&1

:run_maven
call "%MAVEN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
