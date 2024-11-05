package likelion.devbreak.domain;

import jakarta.persistence.*;
import likelion.devbreak.dto.UpdateBlogData;
import lombok.*;

import java.util.List;


@Entity
@Getter @Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blog extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;

    private String blogName;
    private String description;
    private String gitRepoUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> contributors;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles;

    private int favCount;
    private boolean favButton;

    public void UpdateBlog(UpdateBlogData data){
        this.blogName = data.getBlogName();
        this.description = data.getDescription();
        this.gitRepoUrl = data.getGitRepoUrl();
        this.contributors = data.getContributors();
        this.articles = data.getArticles();
        this.favCount = data.getFavCount();
        this.favButton = data.isFavButton();
    }

}
