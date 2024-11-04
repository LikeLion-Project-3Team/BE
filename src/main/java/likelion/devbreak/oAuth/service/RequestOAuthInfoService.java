package likelion.devbreak.oAuth.service;

import likelion.devbreak.oAuth.domain.github.ApiClient;
import likelion.devbreak.oAuth.domain.github.InfoResponse;
import likelion.devbreak.oAuth.domain.github.LoginParams;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService {
	private final ApiClient client;

	public RequestOAuthInfoService(ApiClient client){
		this.client = client;
	}

	public InfoResponse request(LoginParams params) {
		String accessToken = client.requestAccessToken(params);
		return client.requestOauthInfo(accessToken);
	}
}