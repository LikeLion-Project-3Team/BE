package likelion.devbreak.domain;

import jakarta.persistence.*;
import likelion.devbreak.listener.ArticleListener;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(ArticleListener.class)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    private String title;
    private String content;
    private int likeCount;

    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Method to update the article details
    public void updateArticle(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
