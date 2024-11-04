package likelion.devbreak.repository;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Likes;
import likelion.devbreak.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByUserAndArticle(User user, Article article);
}
