package likelion.devbreak.domain;

import jakarta.persistence.*;
import likelion.devbreak.dto.UpdateBlogData;
import likelion.devbreak.listener.BlogListener;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(BlogListener.class)
public class Blog{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;

    private String blogName;
    private String description;
    private String gitRepoUrl;

    @Setter
    private int favCount;
    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;
    @Setter
    private Boolean isFavorited = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Article> articles;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorites> favorites;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlogMember> blogMembers;

    public void updateBlog(UpdateBlogData data){
        this.blogName = data.getBlogName();
        this.description = data.getDescription();
    }

}
