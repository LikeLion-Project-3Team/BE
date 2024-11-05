package likelion.devbreak.domain;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Favorites {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Long blogId;
    private boolean isFavorited;
}
