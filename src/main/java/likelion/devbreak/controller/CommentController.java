package likelion.devbreak.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion.devbreak.domain.dto.request.CommentRequest;
import likelion.devbreak.domain.dto.response.ArticleResponse;
import likelion.devbreak.domain.dto.response.CommentResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    @PostMapping()
    @Operation(summary = "댓글 생성")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestBody CommentRequest commentRequest){
        try {
            CommentResponse comment = commentService.createComment(customUserDetails, commentRequest);
            return ResponseEntity.ok().body(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/comments/{articleId}")
    @Operation(summary = "전체 댓글 조회")
    public ResponseEntity<?> getCommentList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable("articleId") Long articleId){
        try{
            List<CommentResponse> commentResponseList = commentService.getComment(customUserDetails, articleId);
            return ResponseEntity.ok().body(commentResponseList);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "특정 댓글 수정")
    public ResponseEntity<?> updateComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable Long commentId,
                                           @RequestBody CommentRequest commentRequest){
        try{
            CommentResponse comment = commentService.updateComment(customUserDetails, commentId, commentRequest);
            return ResponseEntity.ok().body(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{articleId}/{commentId}")
    @Operation(summary = "특정 댓글 삭제")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable Long articleId,
                                           @PathVariable Long commentId){
        try{
            commentService.deleteComment(customUserDetails,articleId, commentId);
            return ResponseEntity.ok().body("해당 댓글이 삭제되었습니다.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
