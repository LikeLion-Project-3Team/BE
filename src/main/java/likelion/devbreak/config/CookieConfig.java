/*package likelion.devbreak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class CookieConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();

        // 쿠키 이름 설정 (예: accessToken, refreshToken 등)
        serializer.setCookieName("SESSION");

        // SameSite 설정: None, Lax, Strict 중 선택
        serializer.setSameSite("None"); // Cross-Origin 쿠키 허용

        // 쿠키 경로 설정
        serializer.setCookiePath("/");

        // HttpOnly 설정: 자바스크립트에서 접근 불가
        serializer.setUseHttpOnlyCookie(true);

        // Secure 설정: HTTPS 환경에서만 쿠키 전송
        serializer.setUseSecureCookie(true);

        return serializer;
    }
}
*/
