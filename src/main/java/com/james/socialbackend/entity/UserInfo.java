package com.james.socialbackend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 50)
  private String name;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 30)
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
  @Column(unique = true)
  private String username;

  @NotBlank(message = "Email is required")
  @Email
  @Column(unique = true)
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8)
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$")
  private String password;

  @NotBlank(message = "Role is required")
  @Pattern(regexp = "ROLE_(USER|ADMIN)")
  private String roles;

  @ManyToMany
  @JoinTable(
    name = "user_following",
    joinColumns = @JoinColumn(name = "follower_id"),
    inverseJoinColumns = @JoinColumn(name = "following_id")
  )
  private Set<UserInfo> following = new HashSet<>();

  @ManyToMany(mappedBy = "following")
  private Set<UserInfo> followers = new HashSet<>();
}