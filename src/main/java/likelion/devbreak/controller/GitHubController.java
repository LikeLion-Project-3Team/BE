package likelion.devbreak.controller;

import likelion.devbreak.domain.BlogMember;
import likelion.devbreak.oAuth.domain.dto.response.ActivityResponse;
import likelion.devbreak.oAuth.domain.dto.response.ContributorsResponse;
import likelion.devbreak.oAuth.domain.dto.response.RepoResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.oAuth.domain.dto.response.TitleResponse;
import likelion.devbreak.oAuth.domain.github.GitHubClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GitHubController {
    private final GitHubClient gitHubClient;

    public GitHubController(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    //레포 목록 가져오기
    @GetMapping("/repos")
    public Flux<RepoResponse> getUserRepositories(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return gitHubClient.getUserRepositories(customUserDetails);
    }

    //이슈 및 커밋 둘다 가져오기
    @GetMapping("/issues-and-commits")
    public Flux<ActivityResponse> getIssuesAndCommits(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam("html_url") String htmlUrl) {
        Flux<ActivityResponse> issueResponses = gitHubClient.getIssues(customUserDetails, htmlUrl)
                .map(issue -> new ActivityResponse(
                        "Issue",
                        issue.getTitle(),
                        issue.getState(),
                        issue.getDate()
                ));

        Flux<ActivityResponse> commitResponses = gitHubClient.getCommits(customUserDetails, htmlUrl)
                .map(commit -> new ActivityResponse(
                        "Commit",
                        commit.getCommit().getMessage(),
                        null,
                        commit.getCommit().getDate()
                ));

        return Flux.merge(issueResponses, commitResponses);
    }

    @GetMapping("/issues-and-commits/title")
    public Flux<TitleResponse> getTitle(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam("html_url") String htmlUrl) {
        Flux<TitleResponse> issueResponses = gitHubClient.getIssues(customUserDetails, htmlUrl)
                .map(issue -> new TitleResponse(
                        "Issue",
                        issue.getTitle()
                ));

        Flux<TitleResponse> commitResponses = gitHubClient.getCommits(customUserDetails, htmlUrl)
                .map(commit -> new TitleResponse(
                        "Commit",
                        commit.getCommit().getMessage()
                ));

        return Flux.merge(issueResponses, commitResponses);
    }

}
