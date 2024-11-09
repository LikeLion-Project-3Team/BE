package likelion.devbreak.domain.dto.response;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ArticleResponse {
    private Long articleId;
    private Long blogId;
    private Long userId;
    private String title;
    private String blogName;
    private String content;
    private int likeCount;
    private Boolean likeButton;
    private String createdAt;
    private String updatedAt;

    public ArticleResponse(Long articleId, Long blogId,Long userId, String title, String blogName, String content, int likeCount, Boolean likeButton, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.articleId = articleId;
        this.blogId = blogId;
        this.userId = userId;
        this.title = title;
        this.blogName = blogName;
        this.content = content;
        this.likeCount = likeCount;
        this.likeButton = likeButton;
        this.createdAt = createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
        this.updatedAt = updatedAt != null ? updatedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }
}
