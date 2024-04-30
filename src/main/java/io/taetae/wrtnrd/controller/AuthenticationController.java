package io.taetae.wrtnrd.controller;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import io.taetae.wrtnrd.domain.dto.AuthenticationRequestDto;
import io.taetae.wrtnrd.domain.dto.AuthenticationResponseDto;
import io.taetae.wrtnrd.domain.dto.RegisterRequestDto;
import io.taetae.wrtnrd.domain.dto.RegisterResponseDto;
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
  public void authenticate(HttpServletResponse response, @RequestBody AuthenticationRequestDto requestDto) {

    AuthenticationResponseDto responseDto = authenticationService.authenticate(requestDto);
    Cookie accessTokenCookie = new Cookie("ac", responseDto.accessToken());
    Cookie refreshTokenCookie = new Cookie("rf", responseDto.refreshToken());

    response.addCookie(accessTokenCookie);
    response.addCookie(refreshTokenCookie);

    response.setStatus(SC_OK);
  }

  @PostMapping("/refresh-token")
  public void refresh(HttpServletRequest request, HttpServletResponse response/*, @RequestBody AuthenticationRequestDto requestDto*/)
      throws IOException {
    authenticationService.refreshToken(request, response);
  }
}
