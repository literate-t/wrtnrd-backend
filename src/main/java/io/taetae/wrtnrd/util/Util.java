package io.taetae.wrtnrd.util;

import static io.taetae.wrtnrd.util.Constant.ACCESS_TOKEN;
import static io.taetae.wrtnrd.util.Constant.BEARER_BEGIN_INDEX;
import static io.taetae.wrtnrd.util.Constant.REFRESH_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.coyote.BadRequestException;

public class Util {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String checkCookieAndGetValue(HttpServletRequest request, String cookieName)
      throws BadRequestException {

    return Util.getCookieValue(request, cookieName)
        .orElse(null);
  }

  private static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {

    Cookie[] cookies = request.getCookies();
    if (null != cookies && 0 < cookies.length) {
      return Arrays.stream(cookies)
          .filter(cookie -> cookieName.equals(cookie.getName()))
          .map(Cookie::getValue)
          .findFirst();
    }

    return Optional.empty();
  }

  public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {

    Cookie[] cookies = request.getCookies();
    if (null != cookies && 0 < cookies.length) {
      return Arrays.stream(cookies)
          .filter(cookie -> cookieName.equals(cookie.getName()))
          .findFirst();
    }

    return Optional.empty();
  }

  public static void invalidateTokenInCookie(HttpServletRequest request, HttpServletResponse response) {
    Util.getCookie(request, ACCESS_TOKEN).ifPresent(cookie -> {
      cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge(0);
      cookie.setPath("/");
      response.addCookie(cookie);
    });
    Util.getCookie(request, REFRESH_TOKEN).ifPresent(cookie -> {
      cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge(0);
      cookie.setPath("/");
      response.addCookie(cookie);
    });
  }

  public static void sendResponse(HttpServletResponse response, Object value) throws IOException {
    objectMapper.writeValue(response.getOutputStream(), value);
  }

  public static boolean checkSameString(String first, String second) {

    if (!(null != first && null != second && !first.isEmpty() && !second.isEmpty())) {
      return false;
    }

    return first.equals(second);
  }

  public static String getBearerToken(String authorization) {
    return null == authorization ? "" : authorization.substring(BEARER_BEGIN_INDEX);
  }
}
