package likelion.devbreak.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Tag(name = "깃허브 제공 API")
public class GitHubController {
    private final GitHubClient gitHubClient;

    public GitHubController(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    //레포 목록 가져오기
    @GetMapping("/repos")
    @Operation(summary = "유저가 속한 레포지토리 목록 조회 API")
    public Flux<RepoResponse> getUserRepositories(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return gitHubClient.getUserRepositories(customUserDetails);
    }

    //이슈 및 커밋 둘다 가져오기
    @GetMapping("/issues-and-commits")
    @Operation(summary = "특정 레포지토리 이슈 & 커밋 조회 API", description = "토큰 필요 X")
    public Flux<ActivityResponse> getIssuesAndCommits(@RequestParam("html_url") String htmlUrl) {
        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        Flux<ActivityResponse> issueResponses = gitHubClient.getIssues(htmlUrl)
                .map(issue -> new ActivityResponse(
                        "Issue",
                        issue.getTitle(),
                        issue.getState(),
                        issue.getCreatedAt()
                ));

        Flux<ActivityResponse> commitResponses = gitHubClient.getCommits(htmlUrl)
                .map(commit -> new ActivityResponse(
                        "Commit",
                        commit.getCommit().getMessage(),
                        null,
                        commit.getCommit().getAuthor().getDate()
                ));

        return Flux.merge(issueResponses, commitResponses)
                // ISO 8601 형식 날짜를 LocalDateTime으로 변환 후 정렬
                .sort((a, b) -> {
                    LocalDateTime dateA = LocalDateTime.parse(a.getDate(), isoFormatter);
                    LocalDateTime dateB = LocalDateTime.parse(b.getDate(), isoFormatter);
                    return dateB.compareTo(dateA); // 최신순 정렬
                })
                // 정렬 후 'yyyy.MM.dd' 형식으로 변환
                .map(activity -> {
                    LocalDateTime isoDate = LocalDateTime.parse(activity.getDate(), isoFormatter);
                    String formattedDate = isoDate.format(customFormatter);
                    activity.setDate(formattedDate); // 반환용 날짜 형식으로 변환
                    return activity;
                });
    }

    @GetMapping("/issues-and-commits/title")
    @Operation(summary = "특정 레포지토리 이슈 & 커밋 타이틀 목록 조회 API")
    public Flux<TitleResponse> getTitle(@RequestParam("html_url") String htmlUrl) {
        Flux<TitleResponse> issueResponses = gitHubClient.getIssues(htmlUrl)
                .map(issue -> new TitleResponse(
                        "Issue",
                        issue.getTitle()
                ));

        Flux<TitleResponse> commitResponses = gitHubClient.getCommits(htmlUrl)
                .map(commit -> new TitleResponse(
                        "Commit",
                        commit.getCommit().getMessage()
                ));

        return Flux.merge(issueResponses, commitResponses);
    }

}
