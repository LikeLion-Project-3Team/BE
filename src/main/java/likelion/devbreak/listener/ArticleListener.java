package likelion.devbreak.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;

import java.time.LocalDateTime;

public class ArticleListener {
    @PrePersist
    public void prePersist(Article article) {
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        article.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(Article article) {
        article.setUpdatedAt(LocalDateTime.now());
    }
}
