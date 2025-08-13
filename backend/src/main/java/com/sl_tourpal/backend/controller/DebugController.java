package com.sl_tourpal.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> getAuthInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> info = new HashMap<>();
        
        if (auth == null) {
            info.put("authentication", "null");
            return ResponseEntity.ok(info);
        }
        
        info.put("name", auth.getName());
        info.put("principal", auth.getPrincipal().getClass().getSimpleName());
        info.put("authenticated", auth.isAuthenticated());
        info.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(info);
    }
}
