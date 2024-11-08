package likelion.devbreak.service;

import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.BlogMember;
import likelion.devbreak.domain.Favorites;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.response.BlogResponse;
import likelion.devbreak.domain.dto.response.BlogEventResponse;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import likelion.devbreak.domain.dto.response.BreakThrough;
import likelion.devbreak.domain.dto.response.GetBlogResponse;
import likelion.devbreak.dto.UpdateBlogData;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.oAuth.domain.github.GitHubClient;
import likelion.devbreak.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final GlobalService globalService;
    private final GitHubClient gitHubClient;
    private final BlogMemberRepository blogMemberRepository;
    private final ArticleRepository articleRepository;
    private final FavoritesRepository favoritesRepository;

    // 블로그 생성 관련 서비스

    public BlogResponse addBlog(CustomUserDetails customUserDetails, UpdateBlogRequest request) {
        User user = globalService.findUser(customUserDetails);
        blogRepository.findByGitRepoUrl(request.getGitRepoUrl()).orElseThrow(() -> new RuntimeException("이미 해당 레포지토리의 블로그가 존재합니다."));

        Blog blog = Blog.builder()
                .user(user)
                .blogName(request.getBlogName())
                .description(request.getDescription())
                .gitRepoUrl(request.getGitRepoUrl())
                .favCount(0)
                .build();
        blogRepository.save(blog);
        List<BlogMember> members = gitHubClient.getContributors(customUserDetails, blog.getGitRepoUrl(), blog.getId());

        return BlogResponse.createWith(blog, members);
    }

    //블로그 목록 불러오기
    public List<BlogEventResponse> getAllBlogEvents(CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);

        List<Blog> blogIdList = user.getBlogMembers()
                .stream()
                .map(BlogMember::getBlog)
                .collect(Collectors.toList());

        return blogIdList
                .stream()
                .map(blog -> new BlogEventResponse(blog.getId(), blog.getBlogName(), blog.getDescription(), blog.getGitRepoUrl()))
                .collect(Collectors.toList());
    }

    // 특정 블로그 검색 관련 서비스

    public GetBlogResponse getBlog(CustomUserDetails customUserDetails, Long blogId) {
        globalService.findUser(customUserDetails);
        Blog blog = globalService.findBlogById(blogId);

        Boolean isFavorited = favoritesRepository.findByUserIdAndBlogId(customUserDetails.getId(), blogId)
                .map(Favorites::getIsFavorited)
                .orElse(false);

        List<BlogMember> members = blogMemberRepository.findBlogMemberByBlogId(blogId);
        List<BreakThrough> breakThroughs = getArticles(blogId);

        return GetBlogResponse.createWith(blog,members,breakThroughs,isFavorited);
    }

    // 블로그 수정 관련 서비스
    public GetBlogResponse updateBlog(CustomUserDetails customUserDetails, Long blogId, UpdateBlogRequest request){
        User user = globalService.findUser(customUserDetails);
        Blog blog = globalService.findBlogById(blogId);

        user.getBlogMembers().stream().map(BlogMember::getBlog).filter(bm -> bm.getId().equals(blogId))
                .findFirst().orElseThrow(() -> new RuntimeException("수정 권한이 없습니다."));

        blog.updateBlog(UpdateBlogData.createWith(request));
        blogRepository.save(blog);

        Boolean isFavorited = favoritesRepository.findByUserIdAndBlogId(customUserDetails.getId(), blogId)
                .map(Favorites::getIsFavorited)
                .orElse(false);

        List<BlogMember> members = blogMemberRepository.findBlogMemberByBlogId(blogId);

        List<BreakThrough> breakThroughs = getArticles(blogId);

        return GetBlogResponse.createWith(blog,members,breakThroughs, isFavorited);
    }

    //즐겨찾기 기능
    public GetBlogResponse favoriteToggle(CustomUserDetails customUserDetails, Long blogId) {
        User user = globalService.findUser(customUserDetails);
        Blog blog = globalService.findBlogById(blogId);

        Favorites favorite = favoritesRepository.findByUserIdAndBlogId(user.getId(), blogId)
                .orElseGet(() -> new Favorites(false, user, blog));

        favorite.setIsFavorited(!favorite.getIsFavorited());

        blog.setFavCount(blog.getFavCount() + (favorite.getIsFavorited() ? 1 : -1));

        favoritesRepository.save(favorite);
        blogRepository.save(blog);

        List<BlogMember> members = blogMemberRepository.findBlogMemberByBlogId(blogId);
        List<BreakThrough> breakThroughs = getArticles(blogId);

        return GetBlogResponse.createWith(blog, members, breakThroughs, favorite.getIsFavorited());
    }
    @Transactional
    public void deleteBlog(CustomUserDetails customUserDetails, Long blogId){
        User user = globalService.findUser(customUserDetails);
        Blog blog = globalService.findBlogById(blogId);

        if(blog.getUser().getId() != user.getId()){
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        blogRepository.deleteById(blogId);
    }


    //breakThrough 목록 조회
    public List<BreakThrough> getArticles(Long blogId){
        return articleRepository.findArticleByBlog_Id(blogId)
                .stream()
                .map(article -> new BreakThrough(article.getId(), article.getTitle(), article.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
