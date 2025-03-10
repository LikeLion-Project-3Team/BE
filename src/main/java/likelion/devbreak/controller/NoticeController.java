package likelion.devbreak.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion.devbreak.domain.dto.response.BlogEventResponse;
import likelion.devbreak.domain.dto.response.NoticeResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "알림 관련 API")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping()
    @Operation(summary = "개인 모든 알림 조회 API")
    public ResponseEntity<?> getAllNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        try {
            List<NoticeResponse> response = noticeService.getAllNotice(customUserDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "미확인 알림 개수 카운트")
    public ResponseEntity<?> countNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        try {
            int unreadCount = noticeService.getCount(customUserDetails);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{noticeId}")
    @Operation(summary = "알림 조회 상태로 변경")
    public ResponseEntity<?> clickNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @PathVariable Long noticeId){
        try {
            NoticeResponse noticeResponse = noticeService.changeView(customUserDetails, noticeId);
            return ResponseEntity.ok(noticeResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
