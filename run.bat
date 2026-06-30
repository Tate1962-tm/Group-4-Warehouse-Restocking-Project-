@echo off
echo Starting AWRS web app...
echo Browser will open in 20 seconds at http://localhost:8080
echo.
echo IMPORTANT: Use http:// NOT https://
echo Login: admin / admin123
echo.
start "" cmd /c "timeout /t 20 /nobreak >nul && start http://localhost:8080"
mvn spring-boot:run
