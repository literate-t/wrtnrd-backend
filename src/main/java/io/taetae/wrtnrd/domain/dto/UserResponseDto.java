package io.taetae.wrtnrd.domain.dto;

import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.entity.UserRole;
import java.util.ArrayList;
import java.util.List;

public class UserResponseDto {

  // TODO id가 필요한지 고민해볼 것
  private Long id;
  private String username;
  private List<String> roleList = new ArrayList<>();

  public UserResponseDto(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    for(UserRole userRole : user.getUserRoles()) {
      roleList.add(userRole.getRole().getName());
    }
  }
  public static UserResponseDto create(User user) {
    return new UserResponseDto(user);
  }
}
