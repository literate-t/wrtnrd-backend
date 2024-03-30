package io.taetae.wrtnrd.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.taetae.wrtnrd.domain.model.PrincipalUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
  private String tokenEndPoint;

  @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
  private String clientSecret;


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    PrincipalUser principal = (PrincipalUser) authentication.getPrincipal();
    Map<String, Object> accessToken = fetchAccessToken();

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("principal", principal);
    responseMap.put("accessToken", accessToken);

    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    objectMapper.writeValue(response.getWriter(), responseMap);
  }

  private Map<String, Object> fetchAccessToken() {
    if (null == tokenEndPoint && null == clientId && null == clientSecret) {
      throw new InsufficientAuthenticationException("No information about token, client ID, or client secret");
    }

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add("Authorization", getBasicAuth());
//    headers.add("Accept", "application/json");

    // TODO authorization code로 변경 후 리다이렉트는 컨트롤러에서 받을 것
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "client_credentials");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params,
        headers);

    ResponseEntity<Map> response = restTemplate.exchange(tokenEndPoint, HttpMethod.POST, request,
        Map.class);

    return response.getBody();
  }

  private String getBasicAuth() {

    String authToken = clientId + ":" + clientSecret;

    return "Basic " + Base64.getEncoder().encodeToString(authToken.getBytes());
  }
}
