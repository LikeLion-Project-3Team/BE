package likelion.devbreak.domain.dto.response;

import likelion.devbreak.domain.Article;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ArticleListAboutResponse {
    private Long articleId;
    private Long blogId;
    private String title;
    private String blogName;
    private String createdAt;
    private int likeCount;
    private String about;

    public ArticleListAboutResponse(Article article) {
        this.articleId = article.getId();
        this.blogId = article.getBlog().getId();
        this.title = article.getTitle();
        this.blogName = article.getBlog().getBlogName();
        this.likeCount = article.getLikeCount();
        this.about = article.getAbout();
        this.createdAt = article.getCreatedAt() != null ? article.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }
}
