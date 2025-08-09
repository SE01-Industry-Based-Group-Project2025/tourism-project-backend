package com.sl_tourpal.backend.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class StripeConfig {
    
    @Value("${stripe.secret-key}")
    private String stripeSecretKey;
    
    @PostConstruct
    public void initializeStripe() {
        Stripe.apiKey = stripeSecretKey;
        log.info("Stripe API initialized with secret key");
    }
}
