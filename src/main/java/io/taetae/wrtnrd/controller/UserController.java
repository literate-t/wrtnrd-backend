package io.taetae.wrtnrd.controller;

import static io.taetae.wrtnrd.util.Constant.ACCESS_TOKEN;
import static io.taetae.wrtnrd.util.Constant.ACCESS_TOKEN_MISSING;
import static io.taetae.wrtnrd.util.Constant.AUTHOR_CHECK_FAILURE;
import static io.taetae.wrtnrd.util.Constant.AUTHOR_NOT_UPDATED;
import static io.taetae.wrtnrd.util.Constant.AUTHOR_UPDATED;
import static io.taetae.wrtnrd.util.Constant.AVAILABLE_AUTHOR;
import static io.taetae.wrtnrd.util.Constant.INTERNAL_SERVER_ERROR;
import static io.taetae.wrtnrd.util.Constant.NO_PARAM_FROM_REQUEST;
import static io.taetae.wrtnrd.util.Constant.PASSWORD_CHANGED_SUCCESSFULLY;
import static io.taetae.wrtnrd.util.Constant.PASSWORD_CHECKED_SUCCESSFULLY;
import static io.taetae.wrtnrd.util.Constant.PASSWORD_CHECK_FAILURE;
import static io.taetae.wrtnrd.util.Constant.USER_NOT_FOUND_FROM_ACCESS_TOKEN;

import io.taetae.wrtnrd.domain.dto.UserRequestDto;
import io.taetae.wrtnrd.domain.dto.PasswordChangeDto;
import io.taetae.wrtnrd.domain.dto.PasswordCheckDto;
import io.taetae.wrtnrd.filter.jwt.JwtService;
import io.taetae.wrtnrd.service.UserService;
import io.taetae.wrtnrd.util.Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final JwtService jwtService;
  private final UserService userService;

  @PostMapping("/check-password")
  public ResponseEntity<String> checkPassword(@RequestBody PasswordCheckDto passwordDto, HttpServletRequest request) {

    Cookie accessCookie = Util.getCookie(request, ACCESS_TOKEN).orElse(null);

    if (null == accessCookie) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ACCESS_TOKEN_MISSING);
    }

    try {
      String extractUsername = jwtService.extractUsername(accessCookie.getValue());
      boolean result = userService.checkPassword(extractUsername, passwordDto.password());

      if (result) {
        return ResponseEntity.ok(PASSWORD_CHECKED_SUCCESSFULLY);
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(PASSWORD_CHECK_FAILURE);
      }
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Internal server error: " + e.getMessage());
    }
  }

  @PostMapping("/change-password")
  public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDto passwordChangeDto, HttpServletRequest request) {

    Cookie accessCookie = Util.getCookie(request, ACCESS_TOKEN).orElse(null);

    if (null == accessCookie) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ACCESS_TOKEN_MISSING);
    }

    try {
      String extractUsername = jwtService.extractUsername(accessCookie.getValue());
      boolean result = userService.changeUserPassword(extractUsername,
          passwordChangeDto.newPassword());

      if (result) {
        return ResponseEntity.ok(PASSWORD_CHANGED_SUCCESSFULLY);
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(USER_NOT_FOUND_FROM_ACCESS_TOKEN);
      }
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(INTERNAL_SERVER_ERROR +": " + e.getMessage());
    }
  }

  @PostMapping("/check-author-duplicated")
  public ResponseEntity<String> checkAuthorDuplicated(@RequestBody UserRequestDto userRequestDto) {

    if (null == userRequestDto.author() || userRequestDto.author().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(NO_PARAM_FROM_REQUEST);
    }

    boolean result = userService.isAuthorAvailable(userRequestDto.author());
    if (result) {
      return ResponseEntity.ok(AVAILABLE_AUTHOR);
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AUTHOR_CHECK_FAILURE);
    }
  }

  @PostMapping("/change-author")
  public ResponseEntity<String> changeAuthor(@RequestBody UserRequestDto userRequestDto) {

    if (null == userRequestDto.author() || userRequestDto.author().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(NO_PARAM_FROM_REQUEST);
    }

    boolean result = userService.changeAuthor(userRequestDto.userId(), userRequestDto.author());
    if (result) {
      return ResponseEntity.ok(AUTHOR_UPDATED);
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AUTHOR_NOT_UPDATED);
    }
  }
}
