package com.bioinnovate.grh.data.entity;

import com.bioinnovate.grh.data.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// "User" is a reserved word in some SQL implementations
@Entity(name = "UserInfo")
public class User extends AbstractEntity {

	@NotNull
	@Size(min = 1, max = 255)
	@Column(unique = true)
	private String email;

	@NotNull
	@Size(min = 4, max = 255)
	private String passwordHash;


	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@OneToOne(fetch = FetchType.EAGER)
	private UserRole userRoles;

	private boolean active ;


	public User() {
		// An empty constructor is needed for all beans
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String password) {
		this.passwordHash = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserRole getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(UserRole userRoles) {
		this.userRoles = userRoles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


}
