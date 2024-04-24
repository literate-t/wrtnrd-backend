package io.taetae.wrtnrd.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

public class Util {

  public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {

    Cookie[] cookies = request.getCookies();
    if (null != cookies && 0 < cookies.length) {
      return Arrays.stream(cookies)
          .filter(cookie -> cookieName.equals(cookie.getName()))
          .map(Cookie::getValue)
          .findFirst();
    }

    return Optional.empty();
  }
}
