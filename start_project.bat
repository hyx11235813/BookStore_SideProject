@echo off

start "Frontend" cmd /k "cd BookStore_Frontend && npm run serve"

rem Backend uses the "local" profile (application-local.properties, git-ignored)
start "Backend" cmd /k "cd BookStore_Backend && .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local"

pause
