package likelion.devbreak.oAuth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import likelion.devbreak.domain.User;
import likelion.devbreak.oAuth.domain.*;
import likelion.devbreak.oAuth.domain.dto.response.NameResponse;
import likelion.devbreak.oAuth.domain.github.InfoResponse;
import likelion.devbreak.oAuth.domain.github.LoginParams;
import likelion.devbreak.oAuth.repository.RefreshTokenRepository;
import likelion.devbreak.repository.CommentRepository;
import likelion.devbreak.repository.FavoritesRepository;
import likelion.devbreak.repository.LikesRepository;
import likelion.devbreak.repository.UserRepository;
import likelion.devbreak.service.GlobalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginService {
	private final UserRepository userRepository;
	private final AuthTokensGenerator authTokensGenerator;
	private final RequestOAuthInfoService requestOAuthInfoService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	private final LikesRepository likesRepository;
	private final FavoritesRepository favoritesRepository;
	private final GlobalService globalService;
	private final CommentRepository commentRepository;

	@Transactional
	public AuthTokens login(LoginParams params, HttpServletResponse response) throws IOException {
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

		Date accessTokenExpiration = jwtTokenProvider.getTokenExpiration(authTokens.getAccessToken());
		Date refreshTokenExpiration = jwtTokenProvider.getTokenExpiration(authTokens.getRefreshToken());

		long accessTokenMaxAge = (accessTokenExpiration.getTime() - System.currentTimeMillis()) / 1000L;
		long refreshTokenMaxAge = (refreshTokenExpiration.getTime() - System.currentTimeMillis()) / 1000L;

		ResponseCookie accessCookie = createCookie("accessToken", authTokens.getAccessToken(), "devbreak.site", true, accessTokenMaxAge);
		ResponseCookie refreshCookie = createCookie("refreshToken", authTokens.getRefreshToken(), "devbreak.site", true, refreshTokenMaxAge);

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		log.info("Set-Cookie 헤더: {}", accessCookie);
		log.info("Set-Cookie 헤더: {}", refreshCookie);

		response.sendRedirect("https://devbreak-eta.vercel.app");
//		response.sendRedirect("https://devbreak-eta.vercel.app?"+"accessToken="+authTokens.getAccessToken()+"&refreshToken="+authTokens.getRefreshToken());
		return authTokens;
	}

	private ResponseCookie createCookie(String name, String content, String domain, boolean isSecure,long maxage) {
		return ResponseCookie.from(name, content)
				.domain(domain) // 쿠키의 도메인 설정
				.path("/") // 모든 경로에서 유효
				.sameSite("None") // Cross-Origin 요청 허용
				.httpOnly(true) // 자바스크립트 접근 불가
				.secure(isSecure) // HTTPS 환경에서만 동작 (로컬 환경에서는 false로 설정 가능)
				.maxAge(maxage) // 쿠키 유효 기간 설정 (예: 7일)
				.build();
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
	public void logout(Long userId, HttpServletResponse response) {
		refreshTokenRepository.deleteByUserId(userId);

		ResponseCookie accessCookie = createExpiredCookie("accessToken", "devbreak.site", true);
		// 만료된 refreshToken 쿠키 생성
		ResponseCookie refreshCookie = createExpiredCookie("refreshToken", "devbreak.site", true);

		// 응답 헤더에 만료된 쿠키 추가
		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		log.info("Set-Cookie 헤더: {}", accessCookie);
		log.info("Set-Cookie 헤더: {}", refreshCookie);
	}
	private ResponseCookie createExpiredCookie(String name, String domain, boolean isSecure) {
		return ResponseCookie.from(name, null) // 쿠키 값을 null로 설정
				.domain(domain) // 쿠키의 도메인 설정
				.path("/") // 모든 경로에서 유효
				.sameSite("None") // Cross-Origin 요청 허용
				.httpOnly(true) // 자바스크립트 접근 불가
				.secure(isSecure) // HTTPS 환경에서만 동작
				.maxAge(0) // 즉시 만료
				.build();
	}

	@Transactional
	public void delete(CustomUserDetails customUserDetails) {
		User user = globalService.findUser(customUserDetails);
		likesRepository.deleteAllByUser(user);
		favoritesRepository.deleteAllByUser(user);
		commentRepository.deleteAllByUserName(user.getUserName());
		userRepository.deleteById(customUserDetails.getId());
	}
}