package com.james.socialbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.james.socialbackend.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findByUser_Id(int userId);

  @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
  List<Post> findPostsByUserIdOrderByCreatedAtDesc(@Param("userId") int userId);

  List<Post> findAllByOrderByCreatedAtDesc();
}
