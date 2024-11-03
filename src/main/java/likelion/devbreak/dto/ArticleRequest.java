package likelion.devbreak.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRequest {
    private String title;
    private String content;
    private String about;
    private String problem;
    private String solution;
}
