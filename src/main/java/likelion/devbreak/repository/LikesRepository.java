package likelion.devbreak.repository;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Likes;
import likelion.devbreak.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserIdAndArticleId(Long userId, Long articleId);
}
