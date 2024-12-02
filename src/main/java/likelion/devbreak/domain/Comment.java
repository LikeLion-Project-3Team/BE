package likelion.devbreak.domain;

import jakarta.persistence.*;
import likelion.devbreak.listener.ArticleListener;
import likelion.devbreak.listener.CommentListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.EnableCaching;

import java.time.LocalDateTime;

@Getter @Setter
@Entity @NoArgsConstructor
@AllArgsConstructor
@EntityListeners(CommentListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

}
