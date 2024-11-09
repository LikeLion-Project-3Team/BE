package likelion.devbreak.domain.dto.response;


import likelion.devbreak.domain.Article;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ArticleListResponse {
    private Long articleId;
    private Long blogId;
    private String title;
    private String blogName;
    private String createdAt;

    public ArticleListResponse(Article article) {
        this.articleId = getArticleId();
        this.blogId = getBlogId();
        this.title = getTitle();
        this.blogName = getBlogName();
        this.createdAt = createdAt != null ? createdAt.format(String.valueOf(DateTimeFormatter.ofPattern("yyyy.MM.dd"))) : null;
    }
}
