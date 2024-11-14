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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class GitHubClient {
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final GlobalService globalService;
    private final BlogMemberRepository blogMemberRepository;

    public GitHubClient(@Value("${github.base-url}") String baseUrl,
                        @Value("${github.access-token}") String token,
                        UserRepository userRepository,GlobalService globalService,
                        BlogMemberRepository blogMemberRepository) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
        this.userRepository = userRepository;
        this.globalService = globalService;
        this.blogMemberRepository = blogMemberRepository;
    }



    public Flux<RepoResponse> getUserRepositories(CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);
        return webClient.get()
                .uri("/users/{username}/repos?type=all&sort=created&direction=desc", user.getUserName())
                .retrieve()
                .bodyToFlux(RepoResponse.class);
    }

    public Flux<IssueResponse> getIssues(CustomUserDetails customUserDetails, String htmlUrl) {
        User user = globalService.findUser(customUserDetails);
        String decodedUrl = URLDecoder.decode(htmlUrl, StandardCharsets.UTF_8);
        String[] parts = decodedUrl.split("/");
        String owner = parts[parts.length - 2];
        String repoName = parts[parts.length - 1];
        return webClient.get()
                .uri("/repos/{owner}/{repo}/issues?state=all&sort=created&direction=desc", owner, repoName)
                .retrieve()
                .bodyToFlux(IssueResponse.class);
    }

    public Flux<CommitResponse> getCommits(CustomUserDetails customUserDetails, String htmlUrl) {
        globalService.findUser(customUserDetails);

        String decodedUrl = URLDecoder.decode(htmlUrl, StandardCharsets.UTF_8);
        String[] parts = decodedUrl.split("/");
        String owner = parts[parts.length - 2];
        String repoName = parts[parts.length - 1];

        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits", owner, repoName)
                .retrieve()
                .bodyToFlux(CommitResponse.class);
    }


}
