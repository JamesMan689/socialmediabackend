package com.james.socialbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.james.socialbackend.entity.UserInfo;
import com.james.socialbackend.repository.UserInfoRepository;

@Service
@Transactional
public class UserInfoService implements UserDetailsService {

  private final UserInfoRepository repository;
  private final PasswordEncoder passwordEncoder;

  public UserInfoService(UserInfoRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional(readOnly = true)
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

  @Transactional
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

  @Transactional(readOnly = true)
  public List<UserInfo> searchUsers(String query) {
    return repository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
  }

  @Transactional(readOnly = true)
  public Optional<UserInfo> findByUsername(String username) {
    return repository.findByUsername(username);
  }

  @Transactional
  public UserInfo saveUser(UserInfo user) {
    return repository.save(user);
  }

  @Transactional
  public void followUser(String username, String followingUsername) {
    UserInfo user = repository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    UserInfo followingUser = repository.findByUsername(followingUsername).orElseThrow(() -> new RuntimeException("User not found"));

    if (!user.getFollowing().contains(followingUser)) {
      user.getFollowing().add(followingUser);
      repository.save(user);
    }
  }

  @Transactional(readOnly = true)
  public int getFollowerCount(String username) {
    return repository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")).getFollowers().size();
  }

  @Transactional(readOnly = true)
  public int getFollowingCount(String username) {
    return repository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")).getFollowing().size();
  }

  @Transactional
  public void unfollowUser(String username, String followingUsername) {
    UserInfo user = repository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    UserInfo followingUser = repository.findByUsername(followingUsername).orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getFollowing().contains(followingUser)) {
      user.getFollowing().remove(followingUser);
      repository.save(user);
    } else {
      throw new RuntimeException("User is not following this user");
    }
  }
}
