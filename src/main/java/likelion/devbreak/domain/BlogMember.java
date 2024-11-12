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


    public BlogMember(String userName,Blog blog){
        this.userName = userName;
        this.blog = blog;
    }
    public BlogMember(){

    }

}
