# Ensure running as administrator
if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Host "❌ Please run as Administrator" -ForegroundColor Red
    exit
}

# Setup variables
$downloadPath = "D:\Downloads\Documents\jFile\downloads"
$currentUser = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name

Write-Host "Current user: $currentUser" -ForegroundColor Cyan
Write-Host "Download path: $downloadPath" -ForegroundColor Cyan

# Create directory if it doesn't exist
if (!(Test-Path $downloadPath)) {
    New-Item -ItemType Directory -Force -Path $downloadPath
    Write-Host "Created directory: $downloadPath" -ForegroundColor Green
}

# Set permissions for current user
$acl = Get-Acl $downloadPath
$accessRule = New-Object System.Security.AccessControl.FileSystemAccessRule(
    $currentUser,
    "FullControl",
    "ContainerInherit,ObjectInherit",
    "None",
    "Allow"
)
$acl.SetAccessRule($accessRule)
Set-Acl -Path $downloadPath -AclObject $acl

Write-Host "Added permissions for: $currentUser" -ForegroundColor Green

# Test API endpoints
$baseUrl = "http://localhost:8080"
$headers = @{
    "Content-Type" = "application/json"
}

# Sign in
Write-Host "`n🔐 Signing in..." -ForegroundColor Cyan
$signInBody = @{
    username = "testuser"
    password = "password123"
} | ConvertTo-Json

try {
    $signInResponse = Invoke-RestMethod `
        -Uri "$baseUrl/api/auth/signin" `
        -Method Post `
        -Headers $headers `
        -Body $signInBody

    $token = $signInResponse.token
    Write-Host "✅ Login successful!" -ForegroundColor Green
    
    # Test download
    $downloadHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $token"
    }

    $downloadBody = @{
        url = "https://stanford.edu/~cpiech/cs221/handouts/docs/PracticeSolution-1.pdf"
    } | ConvertTo-Json

    Write-Host "`n📥 Testing download..." -ForegroundColor Cyan
    $downloadResponse = Invoke-RestMethod `
        -Uri "$baseUrl/api/downloads" `
        -Method Post `
        -Headers $downloadHeaders `
        -Body $downloadBody

    Write-Host "✅ Download request successful!" -ForegroundColor Green
    Write-Host "Download ID: $($downloadResponse.downloadId)"
    Write-Host "Save Location: $($downloadResponse.metadata)"

} catch {
    Write-Host "`n❌ Error occurred:" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        Write-Host "Error details: $($reader.ReadToEnd())" -ForegroundColor Red
    } else {
        Write-Host "Error: $_" -ForegroundColor Red
    }
}