package com.bioinnovate.grh.data.service;

import java.util.List;
import java.util.Optional;

import com.bioinnovate.grh.data.entity.UserRole;
import com.bioinnovate.grh.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.artur.helpers.CrudService;



@Service
public class UserService extends CrudService<User, Integer> implements UserDetailsService {

	private static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "User has been locked and cannot be modified or deleted";
	private static final String USER_NOT_FOUND = "User not found";
	private  PasswordEncoder passwordEncoder;
	private  UserRepository userRepository;
	private  UserRoleRepository userRoleRepository;

	@Autowired
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,UserRoleRepository userRoleRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.userRoleRepository = userRoleRepository;
	}


    public List<UserRole> findAllRoles(){
		return userRoleRepository.findAll();
	}

	@Override
	protected UserRepository getRepository() {
		return userRepository;
	}



	public String encodePassword(String value) {
		return passwordEncoder.encode(value);
	}


	@Transactional
	public User save(User entity) {
		throwIfUserLocked(entity.getId());
		return super.update(entity);
	}

	@Override
	@Transactional
	public void delete(Integer userId) {
		throwIfUserLocked(userId);
		super.delete(userId);
	}

	private void throwIfUserLocked(Integer userId) {
		if (userId == null) {
			return;
		}

		Optional<User> dbUser = getRepository().findById(userId);
		if (!dbUser.isPresent()) {
		    throw new UserFriendlyDataException(USER_NOT_FOUND);
		}
		if (!dbUser.get().isActive()) {
			throw new UserFriendlyDataException(MODIFY_LOCKED_USER_NOT_PERMITTED);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userAccount = userRepository.findByEmail(username);
		if (userAccount == null) {
			throw new UsernameNotFoundException("User with username [" + username + "] not found in the system");
		}

		return new CustomUserDetails(userAccount);
	}

}
