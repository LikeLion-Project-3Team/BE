package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.BlogMember;
import likelion.devbreak.domain.User;
import likelion.devbreak.dto.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BlogResponse implements ResponseDto {
    private Long blogId;
    private Long userId;
    private String blogName;
    private String description;
    private String gitRepoUrl;
    private Set<String> members;
    private int favCount;
    private Boolean favButton;
    private String createdAt;

    public static BlogResponse createWith(Blog blog, Set<String> members) {
        DateTimeFormatter stringDate = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String formattedCreatedAt = blog.getCreatedAt().format(stringDate);

        return BlogResponse.builder()
                .blogId(blog.getId())
                .userId(blog.getUser().getId())
                .blogName(blog.getBlogName())
                .description(blog.getDescription())
                .gitRepoUrl(blog.getGitRepoUrl())
                .members(members)
                .favCount(blog.getFavCount())
                .favButton(false)
                .createdAt(formattedCreatedAt)
                .build();
    }

}
