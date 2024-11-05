package likelion.devbreak.dto;

import likelion.devbreak.domain.Blog;

import java.time.LocalDateTime;

public class BlogEventResponse {
    private Long id;
    private String name;
    private String description;
    private String gitRepoUrl;
    private int favCount;
    private Boolean favButton;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BlogEventResponse(Blog blog) {
        this.id = blog.getId();
        this.name = blog.getBlogName();
        this.description = blog.getDescription();
        this.gitRepoUrl = blog.getGitRepoUrl();
        this.favCount = blog.getFavCount();
        this.favButton = blog.isFavButton();
    }

}
