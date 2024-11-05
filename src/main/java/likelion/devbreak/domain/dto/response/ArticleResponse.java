package likelion.devbreak.domain.dto.response;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
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
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
