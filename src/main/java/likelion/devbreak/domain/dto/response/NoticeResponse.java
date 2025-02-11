package likelion.devbreak.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class NoticeResponse {
    private Long noticeId;
    private String object;
    private String type;
    private String instigator;
    private Long relatedId;
    private Boolean isViewed;
    private String time;
    private String blogName;

    public NoticeResponse(Long noticeId, String object, String type, String instigator, Long relatedId, Boolean isViewed, LocalDateTime time, String blogName) {
        this.noticeId = noticeId;
        this.object = object;
        this.type = type;
        this.instigator = instigator;
        this.relatedId = relatedId;
        this.isViewed = isViewed;
        this.time = formatTimeDifference(time);
        this.blogName = blogName;
    }

    private String formatTimeDifference(LocalDateTime inputTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(inputTime, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long day = hours / 24;

        if (hours < 1) {
            return minutes + " min ago";
        } else if (day < 1) {
            return hours + " hour ago";
        } else {
            return day + " day ago";
        }
    }

    @JsonGetter("relatedId")
    public Object getDynamicRelatedId() {
        if (type != null) {
            if (type.contains("글")) {
                return Map.of("articleId", relatedId);
            } else if (type.contains("블로그")) {
                return Map.of("blogId", relatedId);
            }
        }
        throw new InvalidTypeException("잘못된 형식입니다");
    }

    public static class InvalidTypeException extends RuntimeException {
        public InvalidTypeException(String message) {
            super(message);
        }
    }
}
