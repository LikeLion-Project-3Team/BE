package likelion.devbreak.oAuth.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class IssueResponse {

    private String title;
    private String state;

    @JsonProperty("created_at") // "created_at"을 "createdAt" 필드로 매핑
    private String createdAt;  // API에서 전달되는 created_at 값은 "yyyy-MM-dd'T'HH:mm:ss'Z'" 형식
}
