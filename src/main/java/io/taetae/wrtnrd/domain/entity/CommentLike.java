package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class CommentLike {

  @Id
  @GeneratedValue(strategy = SEQUENCE)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  private LocalDateTime createdAt;
}
