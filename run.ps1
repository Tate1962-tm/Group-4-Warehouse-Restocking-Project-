Write-Host "Starting AWRS web app..."
Write-Host "Browser will open in 20 seconds at http://localhost:8080"
Write-Host ""
Write-Host "IMPORTANT: Use http:// NOT https://"
Write-Host "Login: admin / admin123"
Write-Host ""

Start-Job -ScriptBlock {
    Start-Sleep -Seconds 20
    Start-Process "http://localhost:8080"
} | Out-Null

mvn spring-boot:run
