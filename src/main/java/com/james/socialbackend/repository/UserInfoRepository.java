package com.james.socialbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.socialbackend.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
  Optional<UserInfo> findByEmail(String email);

  Optional<UserInfo> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
