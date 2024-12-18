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
public class ActivityResponse {
    private String type;           // "Issue" 또는 "Commit"을 나타냄
    private String title;           // 이슈 제목 또는 커밋 메시지
    private String state;           // 이슈 상태 (이슈의 경우에만 사용, 커밋이면 null)
    private String date;       // 포맷된 날짜 문자열

    public ActivityResponse(String type, String title, String state,String date) {
        this.type = type;
        this.title = title;
        this.state = state;
        this.date = date;
    }
}
