package io.taetae.wrtnrd.repository;

import io.taetae.wrtnrd.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
