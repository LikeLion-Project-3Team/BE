package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import likelion.devbreak.dto.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@Builder
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BlogEventResponse implements ResponseDto {
    private Long blogId;
    private String blogName;
    private String description;
    private String gitRepoUrl;

    public BlogEventResponse(Long blogId, String blogName, String description, String gitRepoUrl) {
        this.blogId = blogId;
        this.blogName = blogName;
        this.description = description;
        this.gitRepoUrl = gitRepoUrl;
    }

}
