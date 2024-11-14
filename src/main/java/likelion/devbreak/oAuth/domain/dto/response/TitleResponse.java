package likelion.devbreak.oAuth.domain.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class TitleResponse {
    private String type;           // "Issue" 또는 "Commit"을 나타냄
    private String title;           // 이슈 제목 또는 커밋 메시지

    public TitleResponse(String type, String title) {
        this.type = type;
        this.title = title;
    }
}
