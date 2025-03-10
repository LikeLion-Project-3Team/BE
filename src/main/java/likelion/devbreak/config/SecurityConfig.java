package likelion.devbreak.config;

import likelion.devbreak.oAuth.domain.JwtTokenProvider;
import likelion.devbreak.oAuth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/home/blog", "/api/home/article").permitAll()
                                .requestMatchers("/api/article/breakthrough/**").permitAll()
                                .requestMatchers("/api/comment/comments/**").permitAll()
                                .requestMatchers("/api/blog/non-member").permitAll()
                                .requestMatchers("/api/issues-and-commits").permitAll()
                                .anyRequest().permitAll()) // 그 외 요청은 인증 필요
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 나머지 경로에 대한 기본 설정
        CorsConfiguration securedConfiguration = new CorsConfiguration();
        securedConfiguration.addAllowedOrigin("https://www.devbreak.site");
        securedConfiguration.addAllowedOrigin("https://devbreak.site");
        securedConfiguration.addAllowedOrigin("https://api.devbreak.site");
        securedConfiguration.addAllowedOrigin("https://devbreak-eta.vercel.app");
        securedConfiguration.addAllowedMethod("*");
        securedConfiguration.addAllowedHeader("*");
        securedConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", securedConfiguration);

        return source;
    }
}
