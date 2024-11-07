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
    private Long blogId;
    private String blogName;
    private String description;

    public BlogEventResponse(Blog blog) {
        this.blogId = blog.getId();
        this.blogName = blog.getBlogName();
        this.description = blog.getDescription();
    }
}
