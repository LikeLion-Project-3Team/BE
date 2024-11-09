package likelion.devbreak.service;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.ArticleRepository;
import likelion.devbreak.repository.BlogRepository;
import likelion.devbreak.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
public class GlobalService {
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final ArticleRepository articleRepository;

    public GlobalService(UserRepository userRepository, BlogRepository blogRepository, ArticleRepository articleRepository){
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.articleRepository = articleRepository;
    }

    public User findUser(CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        return user;
    }

    public Blog findBlogById(final Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(()->new NotFoundException("Blog Not Found"));
    }

    public Article findArticleById(final Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(()->new NotFoundException("Article Not Found"));
    }
}
