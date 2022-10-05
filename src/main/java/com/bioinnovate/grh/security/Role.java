package com.bioinnovate.grh.security;

public enum Role {

    ADMIN("ADMIN"),
    USER("USER");

    private final String role;

    private Role(String role) {
        this.role = role;
    }

    public String getRoleName() {
        return this.role;
    }

}
