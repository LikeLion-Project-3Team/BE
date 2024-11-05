package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.dto.ResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetBlogResponse implements ResponseDto {
    private Long id;
    private String blogName;
    private User user;
    private List<Article> articles;
    private String description;
    private String gitRepoUrl;
    private int favCount;
    private Boolean favButton;

    public static GetBlogResponse createWith(Blog blog){
        return GetBlogResponse.builder()
                .id(blog.getId())
                .blogName(blog.getBlogName())
                .user(blog.getUser())
                .articles(blog.getArticles())
                .description(blog.getDescription())
                .gitRepoUrl(blog.getGitRepoUrl())
                .favCount(blog.getFavCount())
                .favButton(blog.isFavButton())
                .build();
    }
}
