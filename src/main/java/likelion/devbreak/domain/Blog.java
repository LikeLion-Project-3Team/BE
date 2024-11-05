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

    @ElementCollection // 이거 재웅이한테 물어보기
    @CollectionTable(name = "member_list", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name="members")
    @Enumerated(EnumType.STRING)
    private List<String> members;

    @ElementCollection // 이거 재웅이한테 물어보기
    @CollectionTable(name = "article_list", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name="members")
    @Enumerated(EnumType.STRING)
    private List<String> articles;

    private int favCount;
    private boolean favButton;

    public void UpdateBlog(UpdateBlogData data){
        this.blogName = data.getBlogName();
        this.description = data.getDescription();
        this.gitRepoUrl = data.getGitRepoUrl();
        this.members = data.getMembers();
        this.articles = data.getArticles();
        this.favCount = data.getFavCount();
        this.favButton = data.isFavButton();
    }

}
