package likelion.devbreak.domain;

import jakarta.persistence.*;
import likelion.devbreak.listener.ArticleListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(ArticleListener.class)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    private String title;
    private String content;
    private String about;
    private String problem;
    private String solution;
    private Long likeCount = 0L;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
