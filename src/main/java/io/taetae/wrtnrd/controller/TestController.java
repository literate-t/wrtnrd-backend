package io.taetae.wrtnrd.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class TestController {

  @GetMapping("/test")
  public String testDemo() {
    return "/test ok";
  }
}
