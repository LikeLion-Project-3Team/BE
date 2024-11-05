package likelion.devbreak.dto;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UpdateBlogData {
    private String blogName;
    private String description;
    private String gitRepoUrl;

    private User user;
    private List<Article> articles;
    private int favCount;
    private boolean favButton;

    public static UpdateBlogData createWith(UpdateBlogRequest request){
        return UpdateBlogData.builder()
                .blogName(request.getBlogName())
                .description(request.getDescription())
                .gitRepoUrl(request.getGitRepoUrl())
                .user(request.getUser())
                .articles(request.getArticles())
                .build();
    }
}
