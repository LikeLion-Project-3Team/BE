package likelion.devbreak.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateBlogRequest {
    private String blogName;
    private String description;
    private String gitRepoUrl;
    private List<String> blogMember;
}