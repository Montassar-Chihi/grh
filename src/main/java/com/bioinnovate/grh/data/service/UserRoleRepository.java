package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
}
