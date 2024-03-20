package io.taetae.wrtnrd.domain.entity;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
public class Role {

  @Id @GeneratedValue(strategy = SEQUENCE)
  @Column(name = "role_Id")
  private Long id;
  private String name;
  private String description;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Role role = (Role) o;
    return Objects.equals(getId(), role.getId()) && Objects.equals(getName(),
        role.getName()) && Objects.equals(getDescription(), role.getDescription());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getDescription());
  }

  @Override
  public String toString() {
    return name;
  }
}
