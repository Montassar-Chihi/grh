package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    String ROLE_PREFIX = "ROLE_";
    private final User userAccount;


    public CustomUserDetails(User userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public String getUsername() {
        return userAccount.getEmail();
    }

    @Override
    public String getPassword() {
        return userAccount.getPasswordHash();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userAccount.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userAccount.isActive();
    }

    @Override
    public boolean isEnabled() {
        return userAccount.isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority(userAccount.getUserRoles().getRole()));

        return list;
    }
}

