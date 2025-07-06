# Test Custom Tour API
# PowerShell script to test the custom tour creation

# Function to test login
function Test-Login {
    $loginUrl = "http://localhost:8080/api/auth/login"
    $loginBody = @{
        email = "admin@admin.com"
        password = "password123"
    } | ConvertTo-Json
    
    try {
        Write-Host "Testing login..." -ForegroundColor Yellow
        $response = Invoke-RestMethod -Uri $loginUrl -Method POST -Body $loginBody -ContentType "application/json"
        Write-Host "Login successful!" -ForegroundColor Green
        Write-Host "Token: $($response.token.Substring(0, 50))..." -ForegroundColor Cyan
        return $response.token
    }
    catch {
        Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Function to test custom tour creation
function Test-CustomTourCreation {
    param([string]$token)
    
    $customTourUrl = "http://localhost:8080/api/tours/custom"
    $customTourBody = @{
        name = "Test Custom Adventure Tour"
        region = "Western Province"
        startDate = "2024-03-15"
        endDate = "2024-03-20"
        durationValue = 5
        durationUnit = "days"
        groupSize = 4
        activities = @("Hiking", "Sightseeing")
        price = 1500.00
        specialRequirements = "We would like vegetarian meals and prefer mountain views"
    } | ConvertTo-Json
    
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    try {
        Write-Host "Testing custom tour creation..." -ForegroundColor Yellow
        $response = Invoke-RestMethod -Uri $customTourUrl -Method POST -Body $customTourBody -Headers $headers
        Write-Host "Custom tour created successfully!" -ForegroundColor Green
        Write-Host "Tour ID: $($response.id)" -ForegroundColor Cyan
        Write-Host "Tour Name: $($response.name)" -ForegroundColor Cyan
        Write-Host "Status: $($response.status)" -ForegroundColor Cyan
        Write-Host "Is Custom: $($response.isCustom)" -ForegroundColor Cyan
        return $response
    }
    catch {
        Write-Host "Custom tour creation failed: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response body: $responseBody" -ForegroundColor Red
        }
        return $null
    }
}

# Function to test getting custom tours
function Test-GetCustomTours {
    param([string]$token)
    
    $getToursUrl = "http://localhost:8080/api/tours/custom/my-tours"
    $headers = @{
        "Authorization" = "Bearer $token"
    }
    
    try {
        Write-Host "Testing get custom tours..." -ForegroundColor Yellow
        $response = Invoke-RestMethod -Uri $getToursUrl -Method GET -Headers $headers
        Write-Host "Retrieved $($response.Count) custom tours" -ForegroundColor Green
        foreach ($tour in $response) {
            Write-Host "  - Tour: $($tour.name) (ID: $($tour.id), Status: $($tour.status))" -ForegroundColor Cyan
        }
        return $response
    }
    catch {
        Write-Host "Get custom tours failed: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Main execution
Write-Host "=== Testing Custom Tour API ===" -ForegroundColor Magenta
Write-Host ""

# Test if server is running
try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "Server is running" -ForegroundColor Green
}
catch {
    Write-Host "Server is not running or not ready. Please start the application first." -ForegroundColor Red
    exit
}

# Run tests
$token = Test-Login
if ($token) {
    Write-Host ""
    $customTour = Test-CustomTourCreation -token $token
    
    Write-Host ""
    $tours = Test-GetCustomTours -token $token
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Magenta
