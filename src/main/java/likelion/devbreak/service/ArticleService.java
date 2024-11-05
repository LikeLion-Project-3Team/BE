package likelion.devbreak.service;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.Likes;
import likelion.devbreak.domain.dto.request.ArticleRequest;
import likelion.devbreak.domain.dto.response.ArticleResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.ArticleRepository;
import likelion.devbreak.repository.BlogRepository;
import likelion.devbreak.repository.UserRepository;
import likelion.devbreak.repository.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    public ArticleService(ArticleRepository articleRepository, LikesRepository likesRepository, UserRepository userRepository, BlogRepository blogRepository){
        this.articleRepository = articleRepository;
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
    }
    public ArticleResponse createArticle(CustomUserDetails customUserDetails, ArticleRequest articleRequest) {
        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new NotFoundException("유저를 발견하지 못했습니다."));
        Blog blog = blogRepository.findById(articleRequest.getBlogId())
                .orElseThrow(() -> new NotFoundException("Blog를 발견하지 못했습니다."));

        Article article = new Article();
        article.setUser(user);
        article.setBlog(blog);
        article.setTitle(articleRequest.getTitle());
        article.setAbout(articleRequest.getAbout());
        article.setProblem(articleRequest.getProblem());
        article.setContent(articleRequest.getContent());

        Article savedArticle = articleRepository.save(article);

        return new ArticleResponse(
                savedArticle.getArticleId(),
                savedArticle.getBlog().getId(),
                savedArticle.getUser().getId(),
                savedArticle.getTitle(),
                savedArticle.getBlog().getBlogName(),
                savedArticle.getAbout(),
                savedArticle.getProblem(),
                savedArticle.getSolution(),
                savedArticle.getContent(),
                savedArticle.getLikeCount(),
                savedArticle.getCreatedAt(),
                savedArticle.getUpdatedAt());
    }

    // 글 수정
    public ArticleResponse updateArticle(Long articleId, Long userId, ArticleRequest articleRequest) {
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        if (!existingArticle.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to update this article");
        }

        existingArticle.setTitle(articleRequest.getTitle());
        existingArticle.setContent(articleRequest.getContent());
        existingArticle.setUpdatedAt(LocalDateTime.now());

        Article updatedArticle = articleRepository.save(existingArticle);
        return toArticleResponse(updatedArticle);
    }

    // 글 삭제
    public void deleteArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        if (!article.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to delete this article");
        }

        articleRepository.delete(article);
    }

    // 특정 글 조회
    public ArticleResponse getArticleById(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        return toArticleResponse(article);
    }

    // 좋아요 및 좋아요 취소 기능
    public ArticleResponse toggleLike(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Likes like = likesRepository.findByUserAndArticle(user, article);

        if (like != null) {
            likesRepository.delete(like);
            article.setLikeCount(article.getLikeCount() - 1);
        } else {
            likesRepository.save(new Likes(user, article));
            article.setLikeCount(article.getLikeCount() + 1);
        }

        Article updatedArticle = articleRepository.save(article);
        return toArticleResponse(updatedArticle);
    }

    // Article 엔티티를 ArticleResponse로 변환하는 메서드
    private ArticleResponse toArticleResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setUserId(article.getUser().getId());
        response.setBlogId(article.getBlog().getId()); // assuming Article has a reference to Blog
        response.setArticleId(article.getId());
        response.setTitle(article.getTitle());
        response.setBlogName("dummy blog name"); // 실제 데이터와 연결 필요
        response.setAbout(article.getAbout());
        response.setProblem(article.getProblem());
        response.setSolution(article.getSolution());
        response.setContent(article.getContent());
        response.setLikeCount(article.getLikeCount());
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());
        return response;
    }
}
