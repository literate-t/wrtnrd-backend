package io.taetae.wrtnrd.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenInMemory {

  public static Map<Long, String> store = new ConcurrentHashMap<>();
}
