package likelion.devbreak.oAuth.domain.github;

import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.response.CommitResponse;
import likelion.devbreak.domain.dto.response.IssueResponse;
import likelion.devbreak.domain.dto.response.RepoResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class GitHubClient {
    private final WebClient webClient;
    private final UserRepository userRepository;

    public GitHubClient(@Value("${github.base-url}") String baseUrl,
                        @Value("${github.access-token}") String token,
                        UserRepository userRepository) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
        this.userRepository = userRepository;
    }

    public Flux<RepoResponse> getUserRepositories(CustomUserDetails customUserDetails) {
        User user = userRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return webClient.get()
                .uri("/users/{username}/repos", user.getUsername())
                .retrieve()
                .bodyToFlux(RepoResponse.class);
    }

    public Flux<IssueResponse> getIssues(CustomUserDetails customUserDetails, RepoResponse repoResponse) {
        User user = userRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return webClient.get()
                .uri("/repos/{owner}/{repo}/issues", user.getUsername(), repoResponse.getHtml_url())
                .retrieve()
                .bodyToFlux(IssueResponse.class);
    }

    public Flux<CommitResponse> getCommits(CustomUserDetails customUserDetails, RepoResponse repoResponse) {
        User user = userRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits", user.getUsername(), repoResponse.getHtml_url())
                .retrieve()
                .bodyToFlux(CommitResponse.class);
    }
}
