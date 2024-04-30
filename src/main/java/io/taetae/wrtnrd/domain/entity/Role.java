package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {

  @Id @GeneratedValue(strategy = SEQUENCE)
  @Column(name = "role_Id")
  private Long id;
  private String roleName;
  private String roleDescription;

  public static Role create(String name, String description) {
    return new Role(null, name, description);
  }
}
