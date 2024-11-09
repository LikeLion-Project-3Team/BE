package likelion.devbreak.controller;

import likelion.devbreak.domain.dto.request.ArticleRequest;
import likelion.devbreak.domain.dto.response.ArticleListResponse;
import likelion.devbreak.domain.dto.response.ArticleResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    // 글 생성
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

    // 특정 글 조회
    @GetMapping("/{articleId}")
    public ResponseEntity<?> getArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            ArticleResponse article = articleService.getArticleById(articleId, customUserDetails);
            return ResponseEntity.ok().body(article);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 글 수정
    @PutMapping("/{articleId}")
    public ResponseEntity<?> updateArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ArticleRequest articleRequest) {
        try {
            ArticleResponse article = articleService.updateArticle(articleId, customUserDetails, articleRequest);
            return ResponseEntity.ok().body(article);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 글 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try {
            articleService.deleteArticle(articleId, customUserDetails);
            return ResponseEntity.ok().body("DELETE SUCCESSFULLY");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 좋아요 및 좋아요 취소
    @PutMapping("/{articleId}/like")
    public ResponseEntity<?> likeToggle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            ArticleResponse articleResponse = articleService.toggleLike(articleId, customUserDetails);
            return ResponseEntity.ok().body(articleResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 15개 최신순으로 글 불러오기 (Page 이용)
    @GetMapping("/breakthrough")
    public ResponseEntity<?> get15ArticlesList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        try {
            Page<ArticleListResponse> response = articleService.getAllArticles(page-1);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
