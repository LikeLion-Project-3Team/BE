package likelion.devbreak.service;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.Likes;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.request.ArticleRequest;
import likelion.devbreak.domain.dto.response.ArticleListResponse;
import likelion.devbreak.domain.dto.response.ArticleResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.ArticleRepository;
import likelion.devbreak.repository.BlogRepository;
import likelion.devbreak.repository.LikesRepository;
import likelion.devbreak.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    private final GlobalService globalService;

    public ArticleService(ArticleRepository articleRepository, LikesRepository likesRepository, UserRepository userRepository, BlogRepository blogRepository, GlobalService globalService){
        this.articleRepository = articleRepository;
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.globalService = globalService;
    }
    public ArticleResponse createArticle(CustomUserDetails customUserDetails, ArticleRequest articleRequest) {
        User user = globalService.findUser(customUserDetails);
        Blog blog = globalService.findBlogById(articleRequest.getBlogId());

        Article article = new Article();
        article.setUser(user);
        article.setBlog(blog);
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setLikeCount(0);

        Likes likes = new Likes(false, user, article);

        Article savedArticle = articleRepository.save(article);
        Likes isLiked = likesRepository.save(likes);

        return toArticleResponse(savedArticle, isLiked);
    }

    // 특정 글 조회
    public ArticleResponse getArticleById(Long articleId, CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);
        Article article = globalService.findArticleById(articleId);

        Likes like = likesRepository.findByUserIdAndArticleId(user.getId(), articleId)
                .orElseThrow(()->new NotFoundException("좋아요 여부를 알 수 없습니다."));

        return toArticleResponse(article, like);
    }

    // 글 수정
    public ArticleResponse updateArticle(Long articleId, CustomUserDetails customUserDetails, ArticleRequest articleRequest) {
        User user = globalService.findUser(customUserDetails);
        globalService.findBlogById(articleRequest.getBlogId());
        Article article = globalService.findArticleById(articleId);

        if(article.getUser().getId() != user.getId()){
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());

        Likes like = likesRepository.findByUserIdAndArticleId(user.getId(), articleId)
                .orElseThrow(()->new NotFoundException("좋아요 여부를 알 수 없습니다."));

        Article updatedArticle = articleRepository.save(article);
        return toArticleResponse(updatedArticle, like);
    }

    // 글 삭제
    public void deleteArticle(Long articleId, CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);
        Article article = globalService.findArticleById(articleId);

        if(article.getUser().getId() != user.getId()){
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        articleRepository.delete(article);
    }



    // 좋아요 및 좋아요 취소 기능
    public ArticleResponse toggleLike(Long articleId, CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);
        Article article = globalService.findArticleById(articleId);

        Likes like = likesRepository.findByUserIdAndArticleId(user.getId(), articleId)
                .orElseThrow(()->new NotFoundException("좋아요 여부를 알 수 없습니다."));
        like.setIsLiked(!like.getIsLiked());

        article.setLikeCount(article.getLikeCount() + (like.getIsLiked() ? 1 : -1));

        likesRepository.save(like);

        Article likedArticle = articleRepository.save(article);
        return toArticleResponse(likedArticle, like);
    }

    // Article 엔티티를 ArticleResponse로 변환하는 메서드
    private ArticleResponse toArticleResponse(Article article, Likes like) {
        ArticleResponse articleResponse = new ArticleResponse(
                article.getId(),
                article.getBlog().getId(),
                article.getUser().getId(),
                article.getTitle(),
                article.getBlog().getBlogName(),
                article.getContent(),
                article.getLikeCount(),
                like.getIsLiked(),
                article.getCreatedAt(),
                article.getUpdatedAt());
        return articleResponse;
    }


    // 글 전체 조회
    public List<ArticleListResponse> getAllArticles(CustomUserDetails customUserDetails) {
        List<Article> articles = articleRepository.findAllByOrderByCreatedAtDesc();

        return articles.stream()
                .map(ArticleListResponse::new)
                .collect(Collectors.toList());

    }

}
