package likelion.devbreak.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://13.124.235.72:8080") // 클라이언트 도메인 추가
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // 필요한 경우 쿠키와 인증 정보 허용
    }
}
