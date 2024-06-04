package io.taetae.wrtnrd.controller;

import io.taetae.wrtnrd.domain.dto.PostLikeRequestDto;
import io.taetae.wrtnrd.domain.dto.PostRequestDto;
import io.taetae.wrtnrd.domain.dto.PostResponseDto;
import io.taetae.wrtnrd.domain.entity.Post;
import io.taetae.wrtnrd.service.PostService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/post")
@RestController
public class PostController {

  private final PostService postService;

  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> getAllPosts(
      @RequestParam Integer page,
      @RequestParam(required = false) String userId) {

    Integer nextPage = postService.getNextPage(null != page ? page : 0);

    Map<String, Object> response = new HashMap<>();
    List<PostResponseDto> list;

    if (null == userId) {
      list = postService.getPosts(null != page ? page : 0);
    } else {
      list = postService.getPostsWithLikes(null != page ? page : 0, Long.parseLong(userId));
    }

    response.put("data", list);
    response.put("nextPage", nextPage);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PostMapping("/new")
  public ResponseEntity<PostResponseDto> post(@RequestBody PostRequestDto postRequestDto) {

    Post post = postService.save(postRequestDto);

    return ResponseEntity.ok(
        new PostResponseDto(
            post.getId(),
            post.getTitle(),
            post.getUser().getAuthor(),
            post.getUser().getDescription(),
            post.getBody(),
            post.getCreatedAt(),
            false
        )
    );
  }

  @Transactional
  @PostMapping("/like")
  public ResponseEntity<Boolean> postLike(@RequestBody PostLikeRequestDto postLikeDto) {

    try {
      boolean result = postService.toggleLike(postLikeDto.userId(), postLikeDto.postId());
      if (result) {
        return ResponseEntity.ok(true);
      } else {
        return ResponseEntity.ok(false);
      }
    } catch (RuntimeException e) {
      log.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
