package likelion.devbreak.controller;

import likelion.devbreak.dto.ArticleRequest;
import likelion.devbreak.dto.ArticleResponse;
import likelion.devbreak.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // 글 생성
    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @RequestParam Long userId,  // userId를 직접 전달
            @RequestBody ArticleRequest articleRequest) {

        ArticleResponse createdArticle = articleService.createArticle(userId, articleRequest);
        return ResponseEntity.ok(createdArticle);
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
