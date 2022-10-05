package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {

	User findByEmail(String email);

}
