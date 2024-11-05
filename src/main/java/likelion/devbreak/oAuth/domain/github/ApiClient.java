package likelion.devbreak.oAuth.domain.github;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Collections;

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
		String decodedCode = URLDecoder.decode(params.makeBody().get("code").get(0), StandardCharsets.UTF_8);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // 응답 형식 설정

		// 엑세스 토큰 요청에 필요한 파라미터 설정
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("code", decodedCode);
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, httpHeaders);

		// response를 String으로 받아온 후 파싱
		String response = restTemplate.postForObject(url, request, String.class);
		System.out.println("Response from GitHub: " + response);
		if (response == null) {
			throw new RuntimeException("Failed to retrieve access token");
		}

		// JSON 파싱
		try {
			// JSON 응답 파싱
			Tokens tokenResponse = objectMapper.readValue(response, Tokens.class);
			return tokenResponse.getAccessToken();
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to parse access token response", e);
		}
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

