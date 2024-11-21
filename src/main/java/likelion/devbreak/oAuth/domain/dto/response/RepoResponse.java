package likelion.devbreak.oAuth.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepoResponse {
    @JsonProperty("html_url")
    private String htmlUrl; // camelCase로 필드 정의
}

