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
        this.articleId = article.getId();
        this.blogId = article.getBlog().getId();
        this.title = article.getTitle();
        this.blogName = article.getBlog().getBlogName();
        this.createdAt = article.getCreatedAt() != null ? article.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }
}
