# Complete Booking Test Script
# Tests the full booking flow: Login -> Tour Selection -> Booking Creation -> Webhook -> Status Confirmation

Write-Host "=== COMPLETE BOOKING AND WEBHOOK TEST ===" -ForegroundColor Green

# Step 1: Login
Write-Host "1. Logging in..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body (@{
    email = "sasirk1513@gmail.com"
    password = "Sasi@1234"
} | ConvertTo-Json)

$token = $loginResponse.token
Write-Host "   Success: Login successful" -ForegroundColor Green

# Step 2: Get available tours
Write-Host "2. Getting available tours..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $tours = Invoke-RestMethod -Uri "http://localhost:8080/api/tours" -Method GET -Headers $headers
    $validTour = $tours | Where-Object { $_.status -eq "UPCOMING" -and $_.price -gt 0 } | Select-Object -First 1
    if ($validTour) {
        Write-Host "   Success: Found valid tour: ID=$($validTour.id), Name=$($validTour.name), Price=$($validTour.price)" -ForegroundColor Green
    } else {
        Write-Host "   Error: No valid tours found" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   Error: Failed to get tours: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Create booking with valid date (within availability range: 30-37 days from current date)
Write-Host "3. Creating booking..." -ForegroundColor Yellow

$bookingRequest = @{
    tourId = $validTour.id
    selectedDate = "2025-09-10"  # This date falls within the availability range
    guestCount = 2
} | ConvertTo-Json

try {
    $bookingResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/bookings/checkout-session" -Method POST -Headers $headers -Body $bookingRequest
    Write-Host "   Success: Booking created successfully!" -ForegroundColor Green
    Write-Host "   Full Response: $($bookingResponse | ConvertTo-Json)" -ForegroundColor Cyan
    Write-Host "   Session ID: $($bookingResponse.sessionId)" -ForegroundColor Cyan
    Write-Host "   Booking ID: $($bookingResponse.bookingId)" -ForegroundColor Cyan
    
    $sessionId = $bookingResponse.sessionId
    $bookingId = $bookingResponse.bookingId
} catch {
    Write-Host "   Error: Booking creation failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Check initial booking status
Write-Host "4. Checking initial booking status..." -ForegroundColor Yellow
try {
    $initialStatus = Invoke-RestMethod -Uri "http://localhost:8080/api/bookings/$bookingId" -Method GET -Headers $headers
    Write-Host "   Initial status: $($initialStatus.status)" -ForegroundColor Green
} catch {
    Write-Host "   Warning: Could not check initial status: $($_.Exception.Message)" -ForegroundColor DarkYellow
}

# Step 5: Test webhook
Write-Host "5. Testing webhook..." -ForegroundColor Yellow

$simpleWebhookPayload = @{
    type = "checkout.session.completed"
    data = @{
        object = @{
            id = $sessionId
            payment_intent = "pi_test_$(Get-Random)"
            payment_status = "paid"
        }
    }
} | ConvertTo-Json -Depth 5

try {
    Write-Host "   Session ID: $sessionId" -ForegroundColor Cyan
    $webhookResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/stripe/webhook" -Method POST -ContentType "application/json" -Body $simpleWebhookPayload
    Write-Host "   Success: Webhook returned: 200 OK" -ForegroundColor Green
} catch {
    Write-Host "   Warning: Webhook failed: $($_.Exception.Message)" -ForegroundColor DarkYellow
}

# Step 6: Check final booking status
Write-Host "6. Checking final booking status..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

try {
    $finalStatus = Invoke-RestMethod -Uri "http://localhost:8080/api/bookings/$bookingId" -Method GET -Headers $headers
    Write-Host "   Final status: $($finalStatus.status)" -ForegroundColor Cyan
    
    if ($finalStatus.status -eq "CONFIRMED") {
        Write-Host "   SUCCESS: Booking confirmed!" -ForegroundColor Green
    } else {
        Write-Host "   Warning: Booking still $($finalStatus.status)" -ForegroundColor DarkYellow
    }
} catch {
    Write-Host "   Error: Could not check final status: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== TEST COMPLETE ===" -ForegroundColor Green
Write-Host "Check backend logs for detailed webhook processing information." -ForegroundColor Yellow

# Expected Flow:
# 1. Login with test user credentials
# 2. Get tours and find one with valid status and price
# 3. Create booking with date in availability range (generates Stripe session)
# 4. Check initial status (should be PENDING)
# 5. Send webhook to simulate payment completion
# 6. Check final status (should be CONFIRMED)
