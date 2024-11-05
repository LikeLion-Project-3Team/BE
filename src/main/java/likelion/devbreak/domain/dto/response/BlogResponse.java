package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.dto.ResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BlogResponse implements ResponseDto {
    private Long blogId;
    private Long userId;
    private String blogName;
    private String description;
    private String gitRepoUrl;
    private User user;
    private int favCount;
    private boolean favButton;
    private LocalDateTime updatedAt;

    public static BlogResponse createWith(Blog blog) {
        return BlogResponse.builder()
                .blogId(blog.getId())
                .userId(blog.getId())
                .blogName(blog.getBlogName())
                .description(blog.getDescription())
                .gitRepoUrl(blog.getGitRepoUrl())
                .user(blog.getUser())
                .favCount(blog.getFavCount())
                .favButton(blog.isFavButton())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }

}
