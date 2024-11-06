package likelion.devbreak.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class IssueResponse {
    private String title;
    private String state;
    private String updatedAt;
    public IssueResponse(String title, String state, LocalDateTime updatedAt) {
        this.title = title;
        this.state = state;
        this.updatedAt = updatedAt != null ? updatedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }
}
