package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Comment {

  @Id @GeneratedValue(strategy = SEQUENCE)
  private Long id;

  private String title;
  private String content;
  private String imagePath;
}
