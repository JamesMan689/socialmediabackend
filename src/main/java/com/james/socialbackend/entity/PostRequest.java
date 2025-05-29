package com.james.socialbackend.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
  private Long id;
  private String caption;
  private String imageUrl;
  private LocalDateTime createdAt;
  private String username;

  public PostRequest(Post post) {
    this.id = post.getId();
    this.caption = post.getCaption();
    this.imageUrl = post.getImageUrl();
    this.createdAt = post.getCreatedAt();
    this.username = post.getUser().getUsername();
  }
}
