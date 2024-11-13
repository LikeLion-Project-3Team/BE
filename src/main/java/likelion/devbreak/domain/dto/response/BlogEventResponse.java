package likelion.devbreak.domain.dto.response;

import likelion.devbreak.domain.Blog;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;


@Getter
@Setter
public class BlogEventResponse {
    private Long blogId;
    private String blogName;
    private String description;
    private String gitRepoUrl;
    private String createdAt;

    public BlogEventResponse(Blog blog) {
        this.blogId = blog.getId();
        this.blogName = blog.getBlogName();
        this.description = blog.getDescription();
        this.gitRepoUrl = blog.getGitRepoUrl();
        this.createdAt = blog.getCreatedAt() != null ? blog.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }

}
