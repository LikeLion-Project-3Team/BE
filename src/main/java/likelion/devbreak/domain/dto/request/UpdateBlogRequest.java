package likelion.devbreak.domain.dto.request;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.User;
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

    private User user;
    private List<Article> articles;
    private int favCount;
    private boolean favButton;
}
