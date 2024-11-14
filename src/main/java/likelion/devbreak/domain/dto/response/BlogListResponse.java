package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.dto.ResponseDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BlogListResponse implements ResponseDto {

    private Long blogId;
    private String blogName;
    private String description;
    private int favCount;

    public BlogListResponse(Blog blog) {
        this.blogId = blog.getId();
        this.blogName = blog.getBlogName();
        this.description = blog.getDescription();
        this.favCount = blog.getFavCount();
    }

    public static BlogListResponse createWithBlogList(Blog blog){
        return new BlogListResponse(
                blog.getId(),
                blog.getBlogName(),
                blog.getDescription(),
                blog.getFavCount()
        );
    }
}
