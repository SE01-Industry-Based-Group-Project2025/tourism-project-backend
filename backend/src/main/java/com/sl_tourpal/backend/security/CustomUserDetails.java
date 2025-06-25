package com.sl_tourpal.backend.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sl_tourpal.backend.domain.Privilege;
import com.sl_tourpal.backend.domain.Role;
import com.sl_tourpal.backend.domain.User;

/**
 * CustomUserDetails adapts our User entity to Spring Security’s UserDetails.
 * It extracts roles and privileges into GrantedAuthorities.
 */
public class CustomUserDetails implements UserDetails {

  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  /**
   * Expose the underlying {@link User} entity for mapping to DTOs.
   */
  public User getUser() {
    return user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();
    for (Role role : user.getRoles()) {
      // Add role itself as authority (ROLE_XXX)
      authorities.add(new SimpleGrantedAuthority(role.getName()));
      // Add each privilege of the role
      for (Privilege privilege : role.getPrivileges()) {
        authorities.add(new SimpleGrantedAuthority(privilege.getName()));
      }
    }
    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return user.isEnabled();
  }
}
// This class implements UserDetails to provide user information to Spring Security.
// It extracts roles and privileges from the User entity and provides them as GrantedAuthorities.