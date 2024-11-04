package likelion.devbreak.oAuth.domain.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class InfoResponse {
	private String login;       // GitHub 사용자명 (username)
	private Long id;            // 사용자 ID
	private String avatarUrl;   // 프로필 이미지 URL
	private String htmlUrl;     // GitHub 프로필 페이지 URL
	private String name;        // 사용자 이름 (optional)
	private String email;       // 이메일 (optional, null일 수 있음)
	private String location;    // 위치 (optional)
	private String bio;
	public String getUsername() {
		return login;
	}

}
