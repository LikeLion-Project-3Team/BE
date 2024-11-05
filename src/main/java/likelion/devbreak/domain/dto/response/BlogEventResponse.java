package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.dto.ResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BlogEventResponse implements ResponseDto {
    private Long id;
    private String blogName;
    private User user;
    private List<Article> articles;
    private String description;
    private String gitRepoUrl;
    private int favCount;
    private Boolean favButton;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BlogEventResponse(Blog blog) {
        this.id = blog.getId();
        this.blogName = blog.getBlogName();
        this.user = blog.getUser();
        this.articles = blog.getArticles();
        this.description = blog.getDescription();
        this.gitRepoUrl = blog.getGitRepoUrl();
        this.favCount = blog.getFavCount();
        this.favButton = blog.isFavButton();
    }

}
