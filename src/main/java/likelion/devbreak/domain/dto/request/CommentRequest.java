package likelion.devbreak.domain.dto.request;

import lombok.Getter;

@Getter
public class CommentRequest {
    private Long articleId;
    private String content;
}
