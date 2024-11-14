package likelion.devbreak.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class BlogMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blogMember_id")
    private Long id;
    private String userName;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public BlogMember(String userName,Blog blog, User user){
        this.userName = userName;
        this.blog = blog;
        this.user = user;
    }
    public BlogMember(){

    }

}
