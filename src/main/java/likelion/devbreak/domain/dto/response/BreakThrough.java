package likelion.devbreak.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class BreakThrough {
    private Long articleId;
    private String articleTitle;
    public String createdAt;

    public BreakThrough(Long articleId, String articleTitle, LocalDateTime createdAt) {
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.createdAt = createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }
}
