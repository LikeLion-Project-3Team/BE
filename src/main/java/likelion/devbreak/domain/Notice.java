package likelion.devbreak.domain;

import jakarta.persistence.*;
import likelion.devbreak.listener.ArticleListener;
import likelion.devbreak.listener.NoticeListener;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(NoticeListener.class)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    private String userName;
    private String type;
    private String instigator;
    private Long relatedId;
    private Boolean isViewed;
    private String blogName;

    private LocalDateTime createdAt;

    public Notice(String userName, String type, String instigator, Long relatedId, Boolean isViewed, String blogName) {
        this.userName = userName;
        this.type = type;
        this.instigator = instigator;
        this.relatedId = relatedId;
        this.isViewed = isViewed;
        this.blogName = blogName;
    }
}
