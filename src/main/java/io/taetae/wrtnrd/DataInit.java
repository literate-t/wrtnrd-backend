package io.taetae.wrtnrd;

import io.taetae.wrtnrd.domain.entity.Post;
import io.taetae.wrtnrd.domain.entity.Role;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.domain.entity.UserRole;
import io.taetae.wrtnrd.repository.PostRepository;
import io.taetae.wrtnrd.repository.RoleRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import io.taetae.wrtnrd.repository.UserRoleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class DataInit {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final PostRepository postRepository;
  private final PasswordEncoder passwordEncoder;

  @Profile("dev")
  @EventListener(ApplicationReadyEvent.class)
  public void init() {

    log.info("Test data init");

    Role role = Role.create("ROLE_USER", "USER");
    roleRepository.save(Role.create("ROLE_ADMIN", "ADMIN"));
    roleRepository.save(Role.create("ROLE_MANAGER", "MANAGER"));
    roleRepository.save(role);

    UserRole userRole = UserRole.create(role);

    User newUser = User.builder()
        .email("gaekoon@gmail.com")
        .password(passwordEncoder.encode("Rlaxogus@2022"))
        .author("good author")
        .description("good description")
        .userRoles(List.of(userRole))
        .build();

    userRole.setUser(newUser);

    User savedUser = userRepository.save(newUser);
    userRoleRepository.save(userRole);

    String body = "I got my peaches out in Georgia (oh, yeah, shit)\n"
        + "I get my weed from California (that's that shit)\n"
        + "I took my chick up to the North, yeah (badass bitch)\n"
        + "I get my light right from the source, yeah (yeah, that's it)\n"
        + "And I see you (oh), the way I breathe you in (in), it's the texture of your skin\n"
        + "I wanna wrap my arms around you, baby, never let you go, oh\n"
        + "And I say, oh, there's nothing like your touch\n"
        + "It's the way you lift me up, yeah\n"
        + "And I'll be right here with you 'til the end";
    String author = "bieber";
    String title = "peaches";
    for (int i = 0; i < 10; ++i) {
      Post newPost = Post.builder()
          .user(savedUser)
          .body(body)
          .title(title + " " + i)
          .createdAt(LocalDateTime.now().toString())
          .build();

      postRepository.save(newPost);
    }
  }

  @Profile("prod")
  @EventListener(ApplicationReadyEvent.class)
  public void roleInit() {
    log.info("Role data init");

    Role role = Role.create("ROLE_USER", "USER");
    roleRepository.save(Role.create("ROLE_ADMIN", "ADMIN"));
    roleRepository.save(Role.create("ROLE_MANAGER", "MANAGER"));
    roleRepository.save(role);
  }
}
