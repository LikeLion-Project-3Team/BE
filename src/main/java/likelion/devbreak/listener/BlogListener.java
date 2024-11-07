package likelion.devbreak.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;

import java.time.LocalDateTime;

public class BlogListener {
    @PrePersist
    public void prePersist(Blog blog) {
        LocalDateTime now = LocalDateTime.now();
        blog.setCreatedAt(now);
        blog.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(Blog blog) {
        blog.setUpdatedAt(LocalDateTime.now());
    }
}
