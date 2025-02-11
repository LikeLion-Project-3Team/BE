package likelion.devbreak.oAuth.domain.github;

import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.BlogMember;
import likelion.devbreak.domain.User;
import likelion.devbreak.oAuth.domain.dto.response.CommitResponse;
import likelion.devbreak.oAuth.domain.dto.response.ContributorsResponse;
import likelion.devbreak.oAuth.domain.dto.response.IssueResponse;
import likelion.devbreak.oAuth.domain.dto.response.RepoResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.BlogMemberRepository;
import likelion.devbreak.repository.UserRepository;
import likelion.devbreak.service.GlobalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class GitHubClient {
    private final WebClient webClient;
    private final GlobalService globalService;

    public GitHubClient(@Value("${github.base-url}") String baseUrl,
                        @Value("${github.access-token}") String token,
                        UserRepository userRepository,GlobalService globalService,
                        BlogMemberRepository blogMemberRepository) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
        this.globalService = globalService;
    }



    public Flux<RepoResponse> getUserRepositories(CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);
        return webClient.get()
                .uri("/users/{username}/repos?type=all&sort=created&direction=desc", user.getUserName())
                .retrieve()
                .bodyToFlux(RepoResponse.class);
    }

    public Flux<IssueResponse> getIssues(String htmlUrl) {
        String requestId = UUID.randomUUID().toString(); // 요청 고유 ID 생성
        log.info("[{}] Received URL: {}", requestId, htmlUrl);

        if (htmlUrl == null || htmlUrl.trim().isEmpty()) {
            log.error("Invalid htmlUrl: null or empty");
            throw new IllegalArgumentException("Invalid htmlUrl: null or empty");
        }

        try {
            String decodedUrl = URLDecoder.decode(htmlUrl, StandardCharsets.UTF_8);
            String[] parts = decodedUrl.split("/");

            log.info("[{}] Decoded URL parts: {}", requestId, Arrays.toString(parts));

            String owner = parts[parts.length - 2];
            String repoName = parts[parts.length - 1];

            log.info("[{}] Owner: {}, Repo: {}", requestId, owner, repoName);

            return webClient.get()
                    .uri("/repos/{owner}/{repo}/issues?state=all&sort=created&direction=desc", owner, repoName)
                    .retrieve()
                    .bodyToFlux(IssueResponse.class);
        } catch (Exception e) {
            log.error("[{}] Error occurred: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    public Flux<CommitResponse> getCommits(String htmlUrl) {

        String decodedUrl = URLDecoder.decode(htmlUrl, StandardCharsets.UTF_8);
        String[] parts = decodedUrl.split("/");
        String owner = parts[parts.length - 2];
        String repoName = parts[parts.length - 1];

        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits", owner, repoName)
                .retrieve()
                .bodyToFlux(CommitResponse.class);
    }

    public boolean isUserExists(CustomUserDetails customUserDetails, String username) {
        globalService.findUser(customUserDetails); // 사용자 검증 등 추가 로직
        try {
            // WebClient 동기 호출
            webClient.get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block(); // block()으로 동기화

            return true; // 호출 성공 시 사용자 존재
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return false; // 404 발생 시 사용자 미존재
            }
            throw new RuntimeException("에러 메세지 : ", e); // 기타 예외 처리
        }
    }


}
