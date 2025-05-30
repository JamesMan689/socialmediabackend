package com.james.socialbackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.james.socialbackend.entity.Post;
import com.james.socialbackend.entity.PostRequest;
import com.james.socialbackend.entity.UserInfo;
import com.james.socialbackend.repository.PostRepository;
import com.james.socialbackend.repository.UserInfoRepository;
import com.james.socialbackend.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostRepository postRepository;
  private final UserInfoRepository userInfoRepository;
  private final JwtService jwtService;

  public PostController(PostRepository postRepository, UserInfoRepository userInfoRepository, JwtService jwtService) {
    this.postRepository = postRepository;
    this.userInfoRepository = userInfoRepository;
    this.jwtService = jwtService;
  }

  // Create a new post
  @PostMapping
  public ResponseEntity<?> createPost(@RequestBody PostRequest request, HttpServletRequest httpRequest) {
    UserInfo user = jwtService.getUserFromRequest(httpRequest);
    Post post = new Post();
    post.setCaption(request.getCaption());
    post.setImageUrl(request.getImageUrl());
    post.setUser(user);
    postRepository.save(post);
    return ResponseEntity.ok(new PostRequest(post));
  }

  // Get all posts
  @GetMapping
  public ResponseEntity<?> getAllPosts() {
    List<Post> posts = postRepository.findAll();
    List<PostRequest> responses = posts.stream()
        .map(PostRequest::new)
        .toList();
    return ResponseEntity.ok(responses);
  }

  // Get posts by user
  @GetMapping("/user")
  public ResponseEntity<?> getPostsByUser(HttpServletRequest httpRequest) {
    UserInfo user = jwtService.getUserFromRequest(httpRequest);
    List<Post> posts = postRepository.findByUser_Id(user.getId());
    List<PostRequest> responses = posts.stream()
        .map(PostRequest::new)
        .toList();
    return ResponseEntity.ok(responses);
  }

  // Edit post
  @PutMapping("/{id}")
  public ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody PostRequest request,
      HttpServletRequest httpServletRequest) {
    UserInfo user = jwtService.getUserFromRequest(httpServletRequest);
    Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

    if (post.getUser().getId() != user.getId()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to edit this post");
    }

    post.setCaption(request.getCaption());
    post.setImageUrl(request.getImageUrl());
    return ResponseEntity.ok(new PostRequest(postRepository.save(post)));
  }

  // Delete post
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(@PathVariable Long id, HttpServletRequest httpServletRequest) {
    UserInfo user = jwtService.getUserFromRequest(httpServletRequest);
    Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

    // Allow admins to delete any post
    if (!user.getRoles().equals("ROLE_ADMIN") && post.getUser().getId() != user.getId()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this post");
    }

    postRepository.delete(post);
    return ResponseEntity.ok("Post deleted successfully");
  }
}
