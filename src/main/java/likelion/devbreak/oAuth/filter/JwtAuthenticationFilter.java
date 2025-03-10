package likelion.devbreak.oAuth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.oAuth.domain.JwtAuthenticationToken;
import likelion.devbreak.oAuth.domain.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j // Lombok을 사용하여 로그 출력
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		// OPTIONS 요청은 인증을 건너뛰고 다음 필터로 넘김
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			log.debug("OPTIONS request - skipping authentication filter.");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			log.debug("Starting JWT authentication filter...");

			// 1. 요청에서 쿠키에서 토큰 추출
			String token = resolveToken(request);
			log.debug("Extracted token: {}", token);

			// 2. 토큰이 없거나 유효하지 않으면 인증 건너뜀
			if (token == null) {
				log.debug("No token found in the request.");
				filterChain.doFilter(request, response);
				return;
			}

			if (!jwtTokenProvider.validateToken(token)) {
				log.warn("Invalid token detected: {}", token);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token.");
				return;
			}

			// 3. 토큰에서 사용자 정보 추출
			String userId = jwtTokenProvider.extractSubject(token);
			log.debug("Extracted userId from token: {}", userId);

			// 4. CustomUserDetails 생성
			CustomUserDetails userDetails = new CustomUserDetails(
					Long.valueOf(userId),
					userId,
					new ArrayList<>() // 권한이 없다면 빈 리스트로 설정
			);

			// 5. SecurityContext에 인증 정보 설정
			JwtAuthenticationToken authentication =
					new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Authentication set in SecurityContext: {}", authentication);
		} catch (ExpiredJwtException e) {
			log.error("Token expired: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
			return;
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT token");
			return;
		} catch (MalformedJwtException e) {
			log.error("Malformed JWT token: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT token");
			return;
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
			return;
		} catch (Exception e) {
			log.error("Unexpected error in authentication filter: {}", e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected authentication error");
			return;
		}

		// 6. 다음 필터로 넘김
		filterChain.doFilter(request, response);
		log.debug("JWT authentication filter completed.");
	}

	private String resolveToken(HttpServletRequest request) {
		// 쿠키에서 "accessToken" 이름의 토큰 추출
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					log.debug("Token found in cookie: {}", cookie.getValue());
					return cookie.getValue();
				}
			}
		}
		log.debug("No token found in cookies.");
		return null;
	}
}



