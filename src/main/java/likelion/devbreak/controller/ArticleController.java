package likelion.devbreak.controller;

import likelion.devbreak.domain.Article;
import likelion.devbreak.service.ArticleService;
import likelion.devbreak.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JwtUtil jwtUtil;

    // 글 생성
    @PostMapping
    public Article createArticle(@RequestBody Article article, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");

        if (!jwtUtil.validateToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        Long userId = jwtUtil.extractUserId(jwtToken);
        return articleService.createArticle(userId, article);
    }

    // 글 수정
    @PutMapping("/{articleId}")
    public Article updateArticle(@PathVariable Long articleId,
                                 @RequestBody Article updatedArticle,
                                 @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");

        if (!jwtUtil.validateToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        Long userId = jwtUtil.extractUserId(jwtToken);
        return articleService.updateArticle(articleId, userId, updatedArticle);
    }

    // 글 삭제
    @DeleteMapping("/{articleId}")
    public String deleteArticle(@PathVariable Long articleId,
                                @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");

        if (!jwtUtil.validateToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        Long userId = jwtUtil.extractUserId(jwtToken);
        articleService.deleteArticle(articleId, userId);
        return "Article deleted successfully";
    }
    // 글조회
    @GetMapping("/{articleId}")
    public Article getArticle(@PathVariable Long articleId,
                              @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");

        if (!jwtUtil.validateToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        // JWT 토큰에서 유저 인증 후 특정 글 조회
        jwtUtil.extractUserId(jwtToken); // userId 추출, 조회에 필요한 경우 사용 가능
        return articleService.getArticleById(articleId);
    }
    // 좋아요/좋아요 취소
    @PutMapping("/{articleId}/like")
    public Article toggleLike(@PathVariable Long articleId,
                              @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");

        if (!jwtUtil.validateToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        Long userId = jwtUtil.extractUserId(jwtToken);
        return articleService.toggleLike(articleId, userId);
    }



}
