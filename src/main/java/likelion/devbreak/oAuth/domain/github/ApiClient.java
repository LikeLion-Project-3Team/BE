package likelion.devbreak.oAuth.domain.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ApiClient {
	private static final String GRANT_TYPE = "authorization_code";

	@Value("${github.client-id}")
	private String clientId;

	@Value("${github.client-secret}")
	private String clientSecret;

	@Value("${github.api-url}")
	private String apiUrl;

	@Value("${github.user-info-url}")
	private String userInfoUrl;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public String requestAccessToken(LoginParams params) {
		String url = apiUrl;

		// GitHub에서 받은 code를 URL 디코딩
		String decoded = URLDecoder.decode(params.makeBody().get("code").get(0), StandardCharsets.UTF_8);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// 엑세스 토큰 요청에 필요한 파라미터 설정
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("code", decoded);
		body.add("grant_type", GRANT_TYPE);
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);

		HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
		Tokens response = restTemplate.postForObject(url, request, Tokens.class);

		if (response == null) {
			throw new RuntimeException("Failed to retrieve access token");
		}
		return response.getAccessToken();
	}

	public InfoResponse requestOauthInfo(String accessToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);

			HttpEntity<Void> request = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);
			return objectMapper.readValue(response.getBody(), InfoResponse.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve user info from GitHub", e);
		}
	}
}

