package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class PostLike {

  @Id
  @GeneratedValue(strategy = SEQUENCE)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  private LocalDateTime createdAt;

  @PrePersist
  private void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public static PostLike create(User user, Post post) {
    PostLike postLike = new PostLike();
    postLike.user = user;
    postLike.post = post;

    return postLike;
  }
}
