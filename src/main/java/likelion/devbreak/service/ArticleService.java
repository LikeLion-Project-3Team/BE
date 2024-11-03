package likelion.devbreak.service;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.Likes;
import likelion.devbreak.repository.ArticleRepository;
import likelion.devbreak.repository.UserRepository;
import likelion.devbreak.repository.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikesRepository LikesRepository;
    // 글 생성
    public Article createArticle(Long userId, Article article) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        article.setUser(user);
        return articleRepository.save(article);
    }

    // 글 수정
    public Article updateArticle(Long articleId, Long userId, Article updatedArticle) {
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        // 사용자가 일치하는지 확인하는 코드
        if (!existingArticle.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to update this article");
        }

        // 기존 데이터를 업데이트 데이터로 교체
        existingArticle.setTitle(updatedArticle.getTitle());
        existingArticle.setContent(updatedArticle.getContent());
        existingArticle.setUpdatedAt(LocalDateTime.now());

        return articleRepository.save(existingArticle);
    }

    // 글 삭제
    public void deleteArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        // 사용자가 일치하는지 확인하는 코드
        if (!article.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to delete this article");
        }

        articleRepository.delete(article);
    }


    // 특정 글 조회
    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));
    }

    // 좋아요 및 좋아요 취소 기능
    public Article toggleLike(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 이미 좋아요를 눌렀는지 확인
        Likes like = LikesRepository.findByUserAndArticle(user, article);

        if (like != null) {
            // 좋아요가 이미 존재하면 취소
            LikesRepository.delete(like);
            article.setLikeCount(article.getLikeCount() - 1);
        } else {
            // 좋아요가 없으면 추가
            LikesRepository.save(new Likes(user, article));
            article.setLikeCount(article.getLikeCount() + 1);
        }

        return articleRepository.save(article);
    }

}
