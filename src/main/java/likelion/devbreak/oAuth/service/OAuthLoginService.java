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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

//		Cookie accessCookie = createCookie("accessToken", authTokens.getAccessToken());
//		Cookie refreshCookie = createCookie("refreshToken", authTokens.getRefreshToken());
//		response.addCookie(accessCookie);
//		response.addCookie(refreshCookie);
//		response.sendRedirect("https://devbreak-eta.vercel.app");
		response.sendRedirect("https://devbreak-eta.vercel.app?"+"accessToken="+authTokens.getAccessToken()+"&refreshToken="+authTokens.getRefreshToken());
		return authTokens;
	}

	private Cookie createCookie(String name, String content){
		Integer hour = 2; // 쿠키 보존 시간
		Cookie newCookie = new Cookie(name, content);
		newCookie.setMaxAge(60*60*2*hour);
		newCookie.setPath("/");
		newCookie.setHttpOnly(true);
		return newCookie;
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
		User user = globalService.findUser(customUserDetails);
		likesRepository.deleteAllByUser(user);
		favoritesRepository.deleteAllByUser(user);
		commentRepository.deleteAllByUserName(user.getUserName());
		userRepository.deleteById(customUserDetails.getId());
	}
}