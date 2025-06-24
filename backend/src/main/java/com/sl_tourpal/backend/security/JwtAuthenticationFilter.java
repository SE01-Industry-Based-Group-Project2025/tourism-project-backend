package com.sl_tourpal.backend.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sl_tourpal.backend.dto.UserResponseDTO;
import com.sl_tourpal.backend.domain.Role;
import java.util.stream.Collectors;
import com.sl_tourpal.backend.security.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    setFilterProcessesUrl("/api/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    try {
      Map<String, String> credentials = new ObjectMapper().readValue(request.getInputStream(), Map.class);
      String email = credentials.get("email");
      String password = credentials.get("password");

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(email, password);
      return authenticationManager.authenticate(authToken);
    } catch (IOException e) {
      throw new RuntimeException("Failed to parse authentication request body", e);
    }
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult
  ) throws IOException {
    UserDetails userDetails = (UserDetails) authResult.getPrincipal();
    String token = jwtUtil.generateToken(userDetails);

    // Map the authenticated user to a DTO to avoid exposing sensitive fields
    CustomUserDetails cud = (CustomUserDetails) userDetails;
    var user = cud.getUser();

    UserResponseDTO dto = new UserResponseDTO();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setPhone(user.getPhone());
    dto.setEnabled(user.isEnabled());
    dto.setRoles(user.getRoles().stream()
                       .map(Role::getName)
                       .collect(Collectors.toSet()));

    // Build response JSON: { "token": "...", "user": { ... } }
    Map<String, Object> respBody = new HashMap<>();
    respBody.put("token", token);
    respBody.put("user", dto);

    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getOutputStream(), respBody);
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed
  ) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    Map<String, String> error = Map.of("message", failed.getMessage());
    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getOutputStream(), error);
  }
}
