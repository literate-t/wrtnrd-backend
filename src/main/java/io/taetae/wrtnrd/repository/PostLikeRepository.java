package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.PostLike;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  Optional<PostLike> findByUserIdAndPostId(Long useId, Long postId);

  @Query("select p.post.id from PostLike p where p.user.id = :userId and p.post.id in :postIds")
  Set<Long> findPostIdByUserIdAndPostIdIn(Long userId, List<Long> postIds);
}
