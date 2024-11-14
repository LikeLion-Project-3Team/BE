package likelion.devbreak.oAuth.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class CommitResponse {
    private String sha;
    private Commit commit;
    private String url;
    private String htmlUrl;

    @Getter
    @Setter
    public static class Commit {
        private Author author;
        private Author committer;
        private String message;
        private String url;

        // 커밋 날짜를 반환
        public String getDate() {
            if (author != null && author.getDate() != null) {
                // ISO 8601 형식의 날짜 문자열을 LocalDateTime으로 파싱 후 'yyyy.MM.dd' 형식으로 변환
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime localDateTime = LocalDateTime.parse(author.getDate(), formatter);
                return localDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }
            return null; // author 또는 date가 null인 경우
        }
    }

    @Getter
    @Setter
    public static class Author {
        private String name;
        private String email;
        private String date; // ISO 8601 형식의 날짜 문자열 (예: 2024-11-13T10:11:12Z)
    }
}

