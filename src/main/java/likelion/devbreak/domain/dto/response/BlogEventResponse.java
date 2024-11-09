package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public static BlogEventResponse createWithBlogList(Blog blog){
        return new BlogEventResponse(
                blog.getId(),
                blog.getBlogName(),
                blog.getDescription()
        );
    }
}
