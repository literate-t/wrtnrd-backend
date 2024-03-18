package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Post {

  @Id
  @GeneratedValue(strategy = SEQUENCE)
  private Long id;

  private String title;
  private String content;
  private String imagePath;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(cascade = ALL, orphanRemoval = true)
  @JoinColumn(name = "post_id")
  private List<Comment> commentList = new ArrayList<>();
}
