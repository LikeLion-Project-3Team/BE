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
    }

    @Getter
    @Setter
    public static class Author {
        private String name;
        private String email;
        private String date; // ISO 8601 형식의 날짜 문자열 (예: 2024-11-13T10:11:12Z)
    }
}

