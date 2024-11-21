package likelion.devbreak.controller;

import likelion.devbreak.domain.dto.request.ArticleRequest;
import likelion.devbreak.domain.dto.response.ArticleListResponse;
import likelion.devbreak.domain.dto.response.ArticleResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // 글 생성
    @PostMapping("/article")
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
    @GetMapping("/article/breakthrough/{articleId}")
    public ResponseEntity<?> getArticle(
            @PathVariable Long articleId) {
        try {
            ArticleResponse article = articleService.getArticleById(articleId);
            return ResponseEntity.ok().body(article);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 글 수정
    @PutMapping("/article/{articleId}")
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
    @DeleteMapping("/article/{articleId}")
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
    @PutMapping("/article/{articleId}/like")
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

    // 글 전체 조회
    @GetMapping("/article/breakthrough")
    public ResponseEntity<?> getArticlesList(
    ) {
        try {
            List<ArticleListResponse> response = articleService.getAllArticles();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 좋아요 순으로 글 목록 조회
    @GetMapping("/home/article")
    public ResponseEntity<?> getArticlesSortedByLikes() {
        try {
            List<ArticleListResponse> response = articleService.getArticlesSortedByLikes();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


    // 유저가 좋아요를 누른 글 목록 조회
    @GetMapping("/home/article/like")
    public ResponseEntity<?> getLikedArticles(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            List<ArticleListResponse> response = articleService.getLikedArticles(customUserDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}
