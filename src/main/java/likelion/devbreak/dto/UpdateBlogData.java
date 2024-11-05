package likelion.devbreak.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UpdateBlogData {
    private String blogName;
    private String description;
    private String gitRepoUrl;

    private List<String> members;
    private List<String> articles;
    private int favCount;
    private boolean favButton;

    public static UpdateBlogData createWith(UpdateBlogRequest request){
        return UpdateBlogData.builder()
                .blogName(request.getBlogName())
                .description(request.getDescription())
                .gitRepoUrl(request.getGitRepoUrl())
                .members(request.getMembers())
                .articles(request.getArticles())
                .build();
    }
}
