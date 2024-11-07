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

    private Long blogId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public BlogMember(User user, Long blogId){
        this.user = user;
        this.blogId = blogId;
    }
    public BlogMember(){

    }

}
