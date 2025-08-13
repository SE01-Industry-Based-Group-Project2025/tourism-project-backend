#!/usr/bin/env python3
"""
Test script to verify Stripe receipt email setup
"""

import requests
import json

def test_booking_creation():
    """Test creating a booking and check if email is properly set"""
    
    # First, login to get a JWT token
    login_data = {
        "email": "john.doe@example.com",  # Replace with a real test user
        "password": "password123"
    }
    
    print("🔐 Attempting to login...")
    login_response = requests.post(
        "http://localhost:8080/api/auth/login",
        json=login_data,
        headers={"Content-Type": "application/json"}
    )
    
    if login_response.status_code != 200:
        print(f"❌ Login failed: {login_response.status_code} - {login_response.text}")
        return
    
    token = login_response.json().get("token")
    print(f"✅ Login successful! Token: {token[:20]}...")
    
    # Create a booking request
    booking_data = {
        "tourId": 1,  # Assuming tour ID 1 exists
        "selectedDate": "2025-08-15",
        "guestCount": 2,
        "specialNote": "Test booking for receipt email verification"
    }
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print("🎫 Creating checkout session...")
    booking_response = requests.post(
        "http://localhost:8080/api/bookings/checkout-session",
        json=booking_data,
        headers=headers
    )
    
    if booking_response.status_code == 200:
        result = booking_response.json()
        print("✅ Checkout session created successfully!")
        print(f"📧 Session ID: {result.get('sessionId')}")
        print(f"🔗 Checkout URL: {result.get('checkoutUrl')}")
        return result.get('sessionId')
    else:
        print(f"❌ Booking creation failed: {booking_response.status_code}")
        print(f"Response: {booking_response.text}")
        return None

if __name__ == "__main__":
    test_booking_creation()
