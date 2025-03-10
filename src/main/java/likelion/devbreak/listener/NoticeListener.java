package likelion.devbreak.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import likelion.devbreak.domain.Notice;

import java.time.LocalDateTime;

public class NoticeListener {
    @PrePersist
    public void prePersist(Notice notice) {
        LocalDateTime now = LocalDateTime.now();
        notice.setCreatedAt(now);
    }
}
