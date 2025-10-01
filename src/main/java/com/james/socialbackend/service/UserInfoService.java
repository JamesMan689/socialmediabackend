package com.james.socialbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.james.socialbackend.entity.UserInfo;
import com.james.socialbackend.repository.UserInfoRepository;

@Service
public class UserInfoService implements UserDetailsService {

  private final UserInfoRepository repository;
  private final PasswordEncoder passwordEncoder;

  public UserInfoService(UserInfoRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    Optional<UserInfo> userDetail = repository.findByEmail(usernameOrEmail);
    if (userDetail.isEmpty()) {
      userDetail = repository.findByUsername(usernameOrEmail);
    }
    if (userDetail.isEmpty()) {
      throw new UsernameNotFoundException("User not found with username/email: " + usernameOrEmail);
    }
    return new UserInfoDetails(userDetail.get());
  }

  public String addUser(UserInfo userInfo) {
    if (repository.existsByEmail(userInfo.getEmail())) {
      throw new RuntimeException("Email already exists");
    }
    if (repository.existsByUsername(userInfo.getUsername())) {
      throw new RuntimeException("Username already exists");
    }
    userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
    repository.save(userInfo);
    return "user added to system";
  }

  
}
