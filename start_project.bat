@echo off

start "Frontend" cmd /k "cd BookStore_Frontend && npm run serve"

start "Backend" cmd /k "cd BookStore_Backend && .\mvnw.cmd spring-boot:run"

pause
