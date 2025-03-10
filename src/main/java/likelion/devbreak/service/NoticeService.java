package likelion.devbreak.service;

import likelion.devbreak.domain.Notice;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.response.NoticeResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final GlobalService globalService;

    public List<NoticeResponse> getAllNotice(CustomUserDetails customUserDetails){
        User user = globalService.findUser(customUserDetails);

        List<NoticeResponse> noticeResponses = noticeRepository.findByUserNameOrderByCreatedAtDesc(user.getUserName())
                .stream()
                .map(notice -> new NoticeResponse(
                            notice.getId(),
                            notice.getUserName(),
                            notice.getType(),
                            notice.getInstigator(),
                            notice.getRelatedId(),
                            notice.getIsViewed(),
                            notice.getCreatedAt(),
                        notice.getBlogName())).collect(Collectors.toList());
        return noticeResponses;
    }

    public int getCount(CustomUserDetails customUserDetails){
        User user = globalService.findUser(customUserDetails);
        int unReadCount = noticeRepository.countByUserNameAndIsViewedFalse(user.getUserName());

        if(unReadCount > 999){
            return 999;
        }

        return unReadCount;
    }
    @Transactional
    public NoticeResponse changeView(CustomUserDetails customUserDetails, Long noticeId){
        User user = globalService.findUser(customUserDetails);
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NotFoundException("해당 알림을 찾을 수 없습니다."));

        notice.setIsViewed(true);

        return new NoticeResponse(
                notice.getId(),
                notice.getUserName(),
                notice.getType(),
                notice.getInstigator(),
                notice.getRelatedId(),
                notice.getIsViewed(),
                notice.getCreatedAt(),
                notice.getBlogName()
        );

    }

}
