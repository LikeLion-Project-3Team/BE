package likelion.devbreak.repository;

import likelion.devbreak.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findArticleByBlog_Id(Long blogId);

    // 최신순으로 정렬
    List<Article> findAllByOrderByCreatedAtDesc();
}
