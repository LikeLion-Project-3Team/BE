package likelion.devbreak.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRequest {
    private Long blogId;
    private String title;
    private String content;
    private String about;
    private String problem;
    private String solution;
}
