package likelion.devbreak.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Comment;

import java.time.LocalDateTime;

public class CommentListener {
    @PrePersist
    public void prePersist(Comment comment) {
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(Comment comment) {
        comment.setUpdatedAt(LocalDateTime.now());
    }
}

