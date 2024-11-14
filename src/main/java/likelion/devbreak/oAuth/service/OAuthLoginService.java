package likelion.devbreak.oAuth.service;

import likelion.devbreak.domain.User;
import likelion.devbreak.oAuth.domain.*;
import likelion.devbreak.oAuth.domain.dto.response.NameResponse;
import likelion.devbreak.oAuth.domain.github.InfoResponse;
import likelion.devbreak.oAuth.domain.github.LoginParams;
import likelion.devbreak.oAuth.repository.RefreshTokenRepository;
import likelion.devbreak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
	private final UserRepository userRepository;
	private final AuthTokensGenerator authTokensGenerator;
	private final RequestOAuthInfoService requestOAuthInfoService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public AuthTokens login(LoginParams params) {
		InfoResponse infoResponse = requestOAuthInfoService.request(params);
		Long userId = findOrCreateMember(infoResponse);

		AuthTokens authTokens = authTokensGenerator.generate(userId);

		Optional<RefreshToken> byUserId = refreshTokenRepository.findByUserId(userId);
		if (byUserId.isPresent()) {
			RefreshToken refreshTokenEntity = byUserId.get();
			refreshTokenEntity.setRefreshToken(authTokens.getRefreshToken());
			refreshTokenRepository.save(refreshTokenEntity);
		} else {
			RefreshToken refreshTokenEntity = new RefreshToken();
			refreshTokenEntity.setRefreshToken(authTokens.getRefreshToken());
			refreshTokenEntity.setUserId(userId);
			refreshTokenRepository.save(refreshTokenEntity);
		}
		return authTokens;
	}

	private Long findOrCreateMember(InfoResponse infoResponse) {
		return userRepository.findByUserName(infoResponse.getUsername())
			.map(User::getId)
			.orElseGet(() -> newMember(infoResponse));
	}

	private Long newMember(InfoResponse infoResponse) {
		User member = User.builder()
			.username(infoResponse.getUsername())
			.build();

		return userRepository.save(member).getId();
	}

	public NameResponse getInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		return new NameResponse(user.getId(), user.getUserName());
	}

	public AuthTokens refresh(String refreshToken) {
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new RuntimeException("Invalid refresh token");
		}

		Long user_id = Long.valueOf(jwtTokenProvider.extractSubject(refreshToken));

		User user = userRepository.findById(user_id)
			.orElseThrow(() -> new RuntimeException("User not found"));

		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
		if (savedRefreshToken.isEmpty()) {
			throw new RuntimeException("Refresh token not found");
		}

		String newAccessToken = jwtTokenProvider.generate(
			String.valueOf(user.getId()), new Date(System.currentTimeMillis() + 3600000)); // 1시간 유효
		String newRefreshToken = jwtTokenProvider.generate(
			String.valueOf(user.getId()), new Date(System.currentTimeMillis() + 604800000)); // 7일 유효

		RefreshToken newRefreshTokenEntity = savedRefreshToken.get();
		newRefreshTokenEntity.setRefreshToken(newRefreshToken);
		refreshTokenRepository.save(newRefreshTokenEntity);

		return new AuthTokens(newAccessToken, newRefreshToken, "Bearer", 3600L);
	}

	@Transactional
	public void logout(Long userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}

	@Transactional
	public void delete(CustomUserDetails customUserDetails) {
		userRepository.deleteById(customUserDetails.getId());
	}
}