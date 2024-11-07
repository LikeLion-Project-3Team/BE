package likelion.devbreak.oAuth.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class CommitResponse {
    private String message;
    private String date;

    public CommitResponse(String message, LocalDateTime updatedAt){
        this.message = message;
        this.date = updatedAt != null ? updatedAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }
}
