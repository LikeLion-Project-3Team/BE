package likelion.devbreak.domain.dto.request;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBlogRequest {
    private String blogName;
    private String description;
    private String gitRepoUrl;
}
