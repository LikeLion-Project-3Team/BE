package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.BlogMember;
import likelion.devbreak.dto.ResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetBlogResponse implements ResponseDto {
    private Long blogId;
    private String blogName;
    private String description;
    private String gitRepoUrl;
    private List<BlogMember> members;
    private int favCount;
    private Boolean favButton;
    private String createdAt;
    private List<BreakThrough> breakThroughs;


    public static GetBlogResponse createWith(Blog blog, List<BlogMember> memberList, List<BreakThrough> breakThroughs, Boolean isFavorited){
        DateTimeFormatter stringDate = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String formattedCreatedAt = blog.getCreatedAt().format(stringDate);

        return GetBlogResponse.builder()
                .blogId(blog.getId())
                .blogName(blog.getBlogName())
                .breakThroughs(breakThroughs)
                .members(memberList)
                .description(blog.getDescription())
                .gitRepoUrl(blog.getGitRepoUrl())
                .favButton(isFavorited)
                .favCount(blog.getFavCount())
                .createdAt(formattedCreatedAt)
                .build();
    }
}
