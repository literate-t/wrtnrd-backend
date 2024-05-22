package io.taetae.wrtnrd.service;

import io.taetae.wrtnrd.domain.dto.PostRequestDto;
import io.taetae.wrtnrd.domain.entity.Post;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.repository.PostRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;

  public Post save(PostRequestDto post) {

    Long userId = post.userId();
    User user = userRepository.findById(userId).orElse(null);

    if (null != user) {
      // TODO User 엔티티에 nickname 추가하고 회원가입 때도 추가하고, author에 넣을 것
      Post newPost = Post.builder().user(user)
          .title(post.title())
          .body(post.body())
          .createdAt(post.createdAt())
          .build();

      return postRepository.save(newPost);
    }

    return null;
  }
}
