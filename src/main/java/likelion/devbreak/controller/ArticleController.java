package likelion.devbreak.controller;

import likelion.devbreak.domain.dto.request.ArticleRequest;
import likelion.devbreak.domain.dto.response.ArticleResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    @PostMapping
    public ResponseEntity<?> createArticle(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ArticleRequest articleRequest) {
        try {
            ArticleResponse createdArticle = articleService.createArticle(customUserDetails, articleRequest);
            return ResponseEntity.ok().body(createdArticle);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 글 수정
    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long articleId,
            @RequestParam Long userId,  // userId를 직접 전달
            @RequestBody ArticleRequest articleRequest) {

        ArticleResponse updatedArticle = articleService.updateArticle(articleId, userId, articleRequest);
        return ResponseEntity.ok(updatedArticle);
    }

    // 글 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity<String> deleteArticle(
            @PathVariable Long articleId,
            @RequestParam Long userId) {  // userId를 직접 전달

        articleService.deleteArticle(articleId, userId);
        return ResponseEntity.ok("Article deleted successfully");
    }

    // 특정 글 조회
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> getArticle(
            @PathVariable Long articleId) {

        ArticleResponse article = articleService.getArticleById(articleId);
        return ResponseEntity.ok(article);
    }

    // 좋아요 및 좋아요 취소
    @PutMapping("/{articleId}/like")
    public ResponseEntity<ArticleResponse> toggleLike(
            @PathVariable Long articleId,
            @RequestParam Long userId) {  // userId를 직접 전달

        ArticleResponse article = articleService.toggleLike(articleId, userId);
        return ResponseEntity.ok(article);
    }
}
