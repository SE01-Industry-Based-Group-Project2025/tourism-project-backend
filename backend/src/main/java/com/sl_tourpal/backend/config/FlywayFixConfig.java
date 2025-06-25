package com.sl_tourpal.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class FlywayFixConfig {

    @Bean
    public CommandLineRunner fixFlywayData(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Check if flyway table exists and has failed migrations
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'sl_tourpal' AND table_name = 'flyway_schema_history'", 
                    Integer.class);
                
                if (count != null && count > 0) {
                    // Remove failed migration record for version 7
                    jdbcTemplate.update("DELETE FROM flyway_schema_history WHERE version = '7' AND success = 0");
                    System.out.println("Cleaned up failed Flyway migration for version 7");
                }
                
                // Update invalid tour status values
                int updated = jdbcTemplate.update("UPDATE tours SET status = 'Incomplete' WHERE status IS NOT NULL AND status NOT IN ('Incomplete', 'Ongoing', 'Completed')");
                System.out.println("Updated " + updated + " tours with invalid status values");
                
                // Set any NULL status values
                int nullUpdated = jdbcTemplate.update("UPDATE tours SET status = 'Incomplete' WHERE status IS NULL");
                System.out.println("Updated " + nullUpdated + " tours with NULL status values");
                
            } catch (Exception e) {
                System.err.println("Error fixing flyway data: " + e.getMessage());
            }
        };
    }
}
