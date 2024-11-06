package likelion.devbreak.controller;

import likelion.devbreak.domain.dto.response.ActivityResponse;
import likelion.devbreak.domain.dto.response.RepoResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.oAuth.domain.github.GitHubClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GitHubController {
    private final GitHubClient gitHubClient;

    public GitHubController(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    //레포 목록 가져오기
    @GetMapping("/api/repos")
    public Flux<RepoResponse> getUserRepositories(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return gitHubClient.getUserRepositories(customUserDetails);
    }

    //이슈 및 커밋 둘다 가져오기
    @GetMapping("/api/issues-and-commits")
    public Flux<ActivityResponse> getIssuesAndCommits(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody RepoResponse repoResponse) {
        Flux<ActivityResponse> issueResponses = gitHubClient.getIssues(customUserDetails, repoResponse)
                .map(issue -> new ActivityResponse(
                        "Issue",
                        issue.getTitle(),
                        issue.getState(),
                        issue.getUpdatedAt()
                ));

        Flux<ActivityResponse> commitResponses = gitHubClient.getCommits(customUserDetails, repoResponse)
                .map(commit -> new ActivityResponse(
                        "Commit",
                        commit.getMessage(),
                        null,
                        commit.getDate()
                ));

        return Flux.merge(issueResponses, commitResponses);
    }
}
