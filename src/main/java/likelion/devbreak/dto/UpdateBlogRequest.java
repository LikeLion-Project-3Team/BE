package likelion.devbreak.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class UpdateBlogRequest {
    private String blogName;
    private String description;
    private String gitRepoUrl;

    private List<String> members;
    private List<String> articles;
    private int favCount;
    private boolean favButton;
}
