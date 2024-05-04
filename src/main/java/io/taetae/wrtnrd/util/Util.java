package io.taetae.wrtnrd.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.coyote.BadRequestException;

public class Util {

  private static ObjectMapper objectMapper = new ObjectMapper();

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

  public static void sendResponse(HttpServletResponse response, Object value) throws IOException {
    objectMapper.writeValue(response.getOutputStream(), value);
  }

  public static boolean checkSameString(String first, String second) {

    if (!(null != first && null != second && !first.isEmpty() && !second.isEmpty())) {
      return false;
    }

    return first.equals(second);
  }
}
