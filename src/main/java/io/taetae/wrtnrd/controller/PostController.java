package io.taetae.wrtnrd.controller;

import io.taetae.wrtnrd.domain.dto.PostRequestDto;
import io.taetae.wrtnrd.domain.dto.PostResponseDto;
import io.taetae.wrtnrd.domain.entity.Post;
import io.taetae.wrtnrd.repository.PostRepository;
import io.taetae.wrtnrd.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/post")
@RestController
public class PostController {

  private final PostService postService;
  private final PostRepository postRepository;

  @GetMapping("/list")
  public ResponseEntity<List<PostResponseDto>> getAllPosts() {

    List<PostResponseDto> postResponseDtoList = postRepository.findAll().stream()
        .map(post -> new PostResponseDto(post.getTitle(), "nickname", "description", post.getBody(), post.getCreatedAt()))
        .toList();

    return ResponseEntity.ok(postResponseDtoList);
  }

  @Transactional
  @PostMapping("/new")
  public ResponseEntity<PostResponseDto> post(@RequestBody PostRequestDto postRequestDto) {

    Post post = postService.save(postRequestDto);

    return ResponseEntity.ok(new PostResponseDto(post.getTitle(), "nickname", "description", post.getBody(), post.getCreatedAt()));
  }
}
