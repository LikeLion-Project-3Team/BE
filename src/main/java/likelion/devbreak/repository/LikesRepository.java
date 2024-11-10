package likelion.devbreak.repository;

import likelion.devbreak.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserIdAndArticleId(Long userId, Long articleId);

    // 사용자가 좋아요를 누른 글 목록 조회
    List<Likes> findByUserIdAndIsLikedTrue(Long userId);
}
