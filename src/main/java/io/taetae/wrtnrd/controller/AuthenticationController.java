package io.taetae.wrtnrd.controller;

import io.taetae.wrtnrd.domain.dto.UserDto;
import io.taetae.wrtnrd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {

  private final UserService userService;

  @PostMapping("/signup")
  public void register(@RequestBody UserDto userDto) {

    userService.register(userDto);
  }
}
