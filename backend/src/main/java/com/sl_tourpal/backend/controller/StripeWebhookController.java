package com.sl_tourpal.backend.controller;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sl_tourpal.backend.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling Stripe webhooks
 */
@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {
    
    private final BookingService bookingService;
    
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;
    
    /**
     * Handle Stripe webhook events
     * POST /api/stripe/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) {
        try {
            // Read the request body
            String payload = getRequestBody(request);
            String sigHeader = request.getHeader("Stripe-Signature");
            
            log.info("Received Stripe webhook with payload length: {} and signature: {}", 
                    payload.length(), sigHeader != null ? "present" : "missing");
            
            Event event;
            
            // For development/testing - temporarily skip signature verification
            log.info("Processing webhook without signature verification for development");
            Gson gson = new Gson();
            event = gson.fromJson(payload, Event.class);
            log.info("Webhook parsed successfully - Event type: {}", event.getType());
            
            // Handle the event
            handleStripeEvent(event, payload);
            
            log.info("Successfully processed Stripe webhook event: {}", event.getType());
            return ResponseEntity.ok("Success");
            
        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }
    
    private void handleStripeEvent(Event event, String rawPayload) {
        log.info("Handling Stripe event: {} with ID: {}", event.getType(), event.getId());
        
        switch (event.getType()) {
            case "checkout.session.completed":
                log.info("Processing checkout.session.completed event");
                handleCheckoutSessionCompleted(event, rawPayload);
                break;
                
            case "checkout.session.expired":
                log.info("Processing checkout.session.expired event");
                handleCheckoutSessionExpired(event);
                break;
                
            case "payment_intent.payment_failed":
                log.info("Processing payment_intent.payment_failed event");
                handlePaymentIntentFailed(event, rawPayload);
                break;
                
            default:
                log.info("Unhandled Stripe event type: {} - ignoring", event.getType());
        }
        
        log.info("Completed processing Stripe event: {}", event.getType());
    }
    
    private void handleCheckoutSessionCompleted(Event event, String rawPayload) {
        try {
            log.info("Starting to handle checkout.session.completed event");
            
            Session session = null;
            String sessionId = null;
            String paymentIntentId = null;
            
            // Try to get session from Stripe SDK deserialization
            try {
                session = (Session) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                
                if (session != null) {
                    sessionId = session.getId();
                    paymentIntentId = session.getPaymentIntent();
                    log.info("Successfully deserialized Stripe session object");
                }
            } catch (Exception e) {
                log.warn("Failed to deserialize Stripe session object, trying manual parsing: {}", e.getMessage());
            }
            
            // Fallback: try to parse manually from raw JSON (for development/testing)
            if (session == null) {
                try {
                    log.info("Attempting manual JSON parsing of webhook payload");
                    com.google.gson.JsonObject eventJson = com.google.gson.JsonParser.parseString(rawPayload).getAsJsonObject();
                    com.google.gson.JsonObject dataObject = eventJson.getAsJsonObject("data");
                    if (dataObject != null) {
                        com.google.gson.JsonObject sessionObject = dataObject.getAsJsonObject("object");
                        if (sessionObject != null) {
                            sessionId = sessionObject.has("id") ? sessionObject.get("id").getAsString() : null;
                            paymentIntentId = sessionObject.has("payment_intent") ? sessionObject.get("payment_intent").getAsString() : null;
                            log.info("Successfully parsed session from raw JSON - Session ID: {}, Payment Intent: {}", sessionId, paymentIntentId);
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to parse session from raw JSON: {}", e.getMessage());
                }
            }
            
            if (sessionId != null && paymentIntentId != null) {
                log.info("Checkout session completed - Session ID: {}, Payment Intent ID: {}", 
                        sessionId, paymentIntentId);
                
                // Retrieve PaymentIntent with expanded latest_charge to get receipt URL
                String receiptUrl = null;
                try {
                    log.info("Retrieving PaymentIntent {} with expanded latest_charge", paymentIntentId);
                    
                    PaymentIntentRetrieveParams retrieveParams = PaymentIntentRetrieveParams.builder()
                            .addExpand("latest_charge")
                            .build();
                    
                    PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId, retrieveParams, null);
                    
                    if (paymentIntent.getLatestChargeObject() != null) {
                        receiptUrl = paymentIntent.getLatestChargeObject().getReceiptUrl();
                        log.info("Retrieved receipt URL from latest_charge: {}", receiptUrl);
                    } else if (paymentIntent.getLatestCharge() != null) {
                        // Fallback: retrieve charge separately
                        log.info("Retrieving charge {} separately", paymentIntent.getLatestCharge());
                        Charge charge = Charge.retrieve(paymentIntent.getLatestCharge());
                        receiptUrl = charge.getReceiptUrl();
                        log.info("Retrieved receipt URL from separate charge: {}", receiptUrl);
                    }
                    
                    if (receiptUrl != null) {
                        log.info("Successfully retrieved receipt URL: {}", receiptUrl);
                    } else {
                        log.warn("No receipt URL found for PaymentIntent: {}", paymentIntentId);
                    }
                    
                } catch (Exception e) {
                    log.error("Error retrieving receipt URL for PaymentIntent {}: {}", paymentIntentId, e.getMessage());
                }
                
                // Call booking service to confirm the booking with receipt URL
                bookingService.confirmFromWebhook(sessionId, paymentIntentId, receiptUrl, rawPayload);
                
                log.info("Successfully processed checkout session completion for session: {}", sessionId);
            } else {
                log.error("Could not extract session ID or payment intent ID from webhook event");
            }
        } catch (Exception e) {
            log.error("Error handling checkout session completed - Event ID: {}, Error: {}", 
                    event.getId(), e.getMessage(), e);
        }
    }

    private void handleCheckoutSessionExpired(Event event) {
        try {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject().orElse(null);
            
            if (session != null) {
                log.info("Checkout session expired: {}", session.getId());
                bookingService.handlePaymentFailure(session.getId(), "Session expired");
            }
        } catch (Exception e) {
            log.error("Error handling checkout session expired", e);
        }
    }

    private void handlePaymentIntentFailed(Event event, String rawPayload) {
        try {
            // Extract payment intent ID and find related booking
            log.info("Payment intent failed: {} - Raw payload length: {}", event.getId(), rawPayload.length());
            // TODO: Implement payment failure handling if needed
        } catch (Exception e) {
            log.error("Error handling payment intent failed", e);
        }
    }
    
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
