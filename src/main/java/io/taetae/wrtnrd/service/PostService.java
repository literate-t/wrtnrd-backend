package io.taetae.wrtnrd.service;

import static io.taetae.wrtnrd.util.Constant.PAGE_TEST_SIZE;

import io.taetae.wrtnrd.domain.dto.PostRequestDto;
import io.taetae.wrtnrd.domain.dto.PostResponseDto;
import io.taetae.wrtnrd.domain.entity.Post;
import io.taetae.wrtnrd.domain.entity.PostLike;
import io.taetae.wrtnrd.domain.entity.User;
import io.taetae.wrtnrd.repository.PostLikeRepository;
import io.taetae.wrtnrd.repository.PostRepository;
import io.taetae.wrtnrd.repository.UserRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;

  public Post save(PostRequestDto post) {

    Long userId = post.userId();
    User user = userRepository.findById(userId).orElse(null);

    if (null != user) {
      Post newPost = Post.builder().user(user)
          .title(post.title())
          .body(post.body())
          .createdAt(post.createdAt())
          .build();

      return postRepository.save(newPost);
    }

    return null;
  }

  public boolean toggleLike(Long userId, Long postId) {

    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
    Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

    return postLikeRepository.findByUserIdAndPostId(userId, postId).map(postLike -> {
      postLikeRepository.delete(postLike);
      return false;
    })
    .orElseGet(() -> {
      PostLike postLike = PostLike.create(user, post);
      postLikeRepository.save(postLike);
      return true;
    });
  }

  public List<PostResponseDto> getPosts(int pageNumber) {
    return postRepository.findAllByOrderByCreatedAtDesc(
            PageRequest.of(pageNumber, PAGE_TEST_SIZE))
        .map(
            post -> new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getUser().getAuthor(),
                post.getUser().getDescription(),
                post.getBody(),
                post.getCreatedAt(),
                false)
        ).toList();
  }

  public @Nullable Integer getNextPage(int pageNumber) {
    long count = postRepository.count();
    long pageCount = (long) Math.ceil((double) count / PAGE_TEST_SIZE);
    return pageNumber + 1 <= pageCount ? pageNumber + 1 : null;
  }

  public List<PostResponseDto> getPostsWithLikes(int pageNumber, Long userId) {

    // 한 페이지의 포스트 리스트 구하기
    Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(
        PageRequest.of(pageNumber, PAGE_TEST_SIZE));

    // 한 페이지의 포스트 id 리스트 구하기
    List<Long> postIds = posts.getContent().stream().map(Post::getId).toList();

    // PostLikeRepository에서 해당 포스트에 좋아요한 포스트 구하기
    Set<Long> postLikeIds = postLikeRepository.findPostIdByUserIdAndPostIdIn(userId, postIds);

    // 포스트 내려줄 때 해당 포스트에 좋아요 체크하기
    return posts.map(post -> new PostResponseDto(
        post.getId(),
        post.getTitle(),
        post.getUser().getAuthor(),
        post.getUser().getDescription(),
        post.getBody(),
        post.getCreatedAt(),
        postLikeIds.contains((post.getId()))
        )
    ).toList();
  }
}
