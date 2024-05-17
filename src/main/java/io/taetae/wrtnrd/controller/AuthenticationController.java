package io.taetae.wrtnrd.controller;

import static io.taetae.wrtnrd.util.Constant.ACCESS_TOKEN;
import static io.taetae.wrtnrd.util.Constant.REFRESH_TOKEN;

import io.taetae.wrtnrd.domain.dto.AuthenticationRequestDto;
import io.taetae.wrtnrd.domain.dto.AuthenticationResponseDto;
import io.taetae.wrtnrd.domain.dto.RegisterRequestDto;
import io.taetae.wrtnrd.domain.dto.RegisterResponseDto;
import io.taetae.wrtnrd.domain.dto.UserResponseDto;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto requestDto) {
    return ResponseEntity.ok(authenticationService.register(requestDto));
  }

  @GetMapping("/check-duplicate-username")
  public ResponseEntity<Boolean> checkDuplicateUsername(@RequestBody String email) {
    return ResponseEntity.ok(authenticationService.isDuplicateEmail(email));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<UserResponseDto> authenticate(HttpServletResponse response, @RequestBody AuthenticationRequestDto requestDto) {

    AuthenticationResponseDto responseDto = authenticationService.authenticate(requestDto);
    Cookie accessTokenCookie = new Cookie("ac", responseDto.accessToken());
    Cookie refreshTokenCookie = new Cookie("rf", responseDto.refreshToken());

    accessTokenCookie.setPath("/");
    refreshTokenCookie.setPath("/");

    response.addCookie(accessTokenCookie);
    response.addCookie(refreshTokenCookie);

    User user = responseDto.user();

    return ResponseEntity.ok(new UserResponseDto(user.getId(), user.getEmail()));
  }

  // TODO remove the http-dependent code from service
  @PostMapping("/new-tokens")
  public void newTokens(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    AuthenticationResponseDto responseDto = authenticationService.refreshToken(request, response);

    Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN, responseDto.accessToken());
    Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, responseDto.refreshToken());

    accessTokenCookie.setPath("/");
    refreshTokenCookie.setPath("/");

    response.addCookie(accessTokenCookie);
    response.addCookie(refreshTokenCookie);
  }
}
