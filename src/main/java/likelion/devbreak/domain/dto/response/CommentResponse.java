package likelion.devbreak.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
@NoArgsConstructor
public class CommentResponse {
    private Long articleId;
    private Long commentId;
    private String userName;
    private String content;
    private String date;
    private Boolean updateButton;
    private Boolean deleteButton;

    public CommentResponse(Long articleId, Long commentId, String userName, String content, LocalDateTime date, Boolean updateButton, Boolean deleteButton) {
        this.articleId = articleId;
        this.commentId = commentId;
        this.userName = userName;
        this.content = content;
        this.date = date != null ? date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
        this.updateButton = updateButton;
        this.deleteButton = deleteButton;
    }
}
