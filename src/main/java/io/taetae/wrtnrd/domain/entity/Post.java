package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Post {

  @Id
  @GeneratedValue(strategy = SEQUENCE)
  private Long id;

  private String title;
  @Column(columnDefinition = "TEXT")
  private String body;
  private String imagePath;
  private String createdAt;
  private String lastModifiedAt;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(cascade = ALL, orphanRemoval = true)
  @JoinColumn(name = "post_id")
  private List<Comment> commentList = new ArrayList<>();
}
