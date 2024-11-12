package likelion.devbreak.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBlogRequest {
    private String blogName;
    private String description;
    private String gitRepoUrl;
}