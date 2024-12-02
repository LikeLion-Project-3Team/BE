package likelion.devbreak.repository;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentByArticle(Article article);
    void deleteAllByUserName(String userName);
}
