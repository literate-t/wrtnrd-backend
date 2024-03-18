package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Follow {

  @Id
  @GeneratedValue(strategy = SEQUENCE)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "follower_id")
  private User follower;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "followee_id")
  private User followee;
}
