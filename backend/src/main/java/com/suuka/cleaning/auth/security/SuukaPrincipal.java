package com.suuka.cleaning.auth.security;

import com.suuka.cleaning.common.enums.Permission;
import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.users.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class SuukaPrincipal implements UserDetails {
    private final UUID id;
    private final String email;
    private final String password;
    private final Role role;
    private final String branch;
    private final String zone;
    private final Collection<? extends GrantedAuthority> authorities;

    public SuukaPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.role = user.getRole();
        this.branch = user.getBranch();
        this.zone = user.getZone();
        this.authorities = user.getPermissions().stream()
                .map(Permission::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public UUID getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getBranch() {
        return branch;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }
}
