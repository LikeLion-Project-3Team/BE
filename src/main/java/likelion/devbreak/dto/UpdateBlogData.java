package likelion.devbreak.dto;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UpdateBlogData {
    private String blogName;
    private String description;

    public static UpdateBlogData createWith(UpdateBlogRequest request){
        return UpdateBlogData.builder()
                .blogName(request.getBlogName())
                .description(request.getDescription())
                .build();
    }
}
