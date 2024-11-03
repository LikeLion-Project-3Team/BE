package likelion.devbreak.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleResponse {
    private Long userId;
    private Long blogId;
    private Long articleId;
    private String title;
    private String blogName;
    private String about;
    private String problem;
    private String solution;
    private String content;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
