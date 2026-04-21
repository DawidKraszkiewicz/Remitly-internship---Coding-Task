@echo off
REM Usage: start.cmd [PORT]
REM Example: start.cmd 9000
REM If PORT is omitted, defaults to 8080.

SET PORT=%1
IF "%PORT%"=="" SET PORT=8080

echo Starting Stock Market application on port %PORT%...
SET "APP_PORT=%PORT%" && docker compose up --build
