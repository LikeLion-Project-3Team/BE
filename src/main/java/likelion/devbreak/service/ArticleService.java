package likelion.devbreak.service;

import likelion.devbreak.domain.*;
import likelion.devbreak.domain.dto.request.ArticleRequest;
import likelion.devbreak.domain.dto.response.ArticleListAboutResponse;
import likelion.devbreak.domain.dto.response.ArticleListResponse;
import likelion.devbreak.domain.dto.response.ArticleResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.*;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final LikesRepository likesRepository;

    private final GlobalService globalService;
    private final NoticeRepository noticeRepository;
    private final FavoritesRepository favoritesRepository;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository, LikesRepository likesRepository, GlobalService globalService, NoticeRepository noticeRepository, FavoritesRepository favoritesRepository, UserRepository userRepository){
        this.articleRepository = articleRepository;
        this.likesRepository = likesRepository;
        this.globalService = globalService;
        this.noticeRepository = noticeRepository;
        this.favoritesRepository = favoritesRepository;
        this.userRepository = userRepository;
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
        article.setAbout(articleRequest.getAbout());
        article.setProblem(articleRequest.getProblem());
        article.setSolution(articleRequest.getSolution());

        Likes likes = new Likes(false, user, article);

        Article savedArticle = articleRepository.save(article);
        Likes isLiked = likesRepository.save(likes);

        List<Notice> noticeList = newArticleNotice(article);

        if(!noticeList.isEmpty()){
            noticeRepository.saveAll(noticeList);
        }

        return toArticleResponse(savedArticle, isLiked);
    }

    // 특정 글 조회
    public ArticleResponse getArticleById(Long articleId) {
        Article article = globalService.findArticleById(articleId);

        return toArticleResponseNoLike(article);
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
        article.setAbout(articleRequest.getAbout());
        article.setProblem(articleRequest.getProblem());
        article.setSolution(articleRequest.getSolution());

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
                .orElseGet(() -> new Likes(false, user, article));
        like.setIsLiked(!like.getIsLiked());

        article.setLikeCount(article.getLikeCount() + (like.getIsLiked() ? 1 : -1));

        likesRepository.save(like);

        if(like.getIsLiked()){
            Notice notice = new Notice(article.getUser().getUserName(),"글 좋아요", user.getUserName(), article.getId(), false, article.getBlog().getBlogName());

            noticeRepository.save(notice);
        }

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
                article.getAbout(),
                article.getProblem(),
                article.getSolution(),
                like.getIsLiked(),
                article.getCreatedAt(),
                article.getUpdatedAt());
        return articleResponse;
    }

    private ArticleResponse toArticleResponseNoLike(Article article) {
        ArticleResponse articleResponse = new ArticleResponse(
                article.getId(),
                article.getBlog().getId(),
                article.getUser().getId(),
                article.getTitle(),
                article.getBlog().getBlogName(),
                article.getContent(),
                article.getLikeCount(),
                article.getAbout(),
                article.getProblem(),
                article.getSolution(),
                false,
                article.getCreatedAt(),
                article.getUpdatedAt());
        return articleResponse;
    }

    // 글 전체 조회
    public List<ArticleListAboutResponse> getAllArticles() {
        List<Article> articles = articleRepository.findAllByOrderByCreatedAtDesc();

        return articles.stream()
                .map(ArticleListAboutResponse::new)
                .collect(Collectors.toList());
    }

    // 좋아요 순으로 상위 10개 글 목록 조회
    public List<ArticleListResponse> getArticlesSortedByLikes() {
        // 상위 10개 글만 조회
        List<Article> articles = articleRepository.findTop10ByOrderByLikeCountDesc();
        return articles.stream()
                .map(ArticleListResponse::new)
                .collect(Collectors.toList());
    }

    //좋아요 누른글 목록 조회
    public List<ArticleListResponse> getLikedArticles(CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);
        List<Likes> likes = likesRepository.findByUserIdAndIsLikedTrue(user.getId());

        return likes.stream()
                .map(like -> new ArticleListResponse(like.getArticle()))
                .collect(Collectors.toList());
    }

    public List<Notice> newArticleNotice(Article article){
        List<Notice> notices = favoritesRepository.findByBlogIdAndIsFavoritedTrue(article.getBlog().getId())
                .stream().map(favorites -> {
                    String userName = favorites.getUser().getUserName();
                    return new Notice(userName, "즐겨찾기 한 blog의 새로운 글", article.getUser().getUserName(), article.getId(), false, article.getBlog().getBlogName());
                }).collect(Collectors.toList());

        return notices;
    }


}
