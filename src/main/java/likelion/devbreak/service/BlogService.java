package likelion.devbreak.service;

import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.BlogMember;
import likelion.devbreak.domain.Favorites;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import likelion.devbreak.domain.dto.response.*;
import likelion.devbreak.dto.UpdateBlogData;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.oAuth.domain.github.GitHubClient;
import likelion.devbreak.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final GlobalService globalService;
    private final BlogMemberRepository blogMemberRepository;
    private final ArticleRepository articleRepository;
    private final FavoritesRepository favoritesRepository;

    // 블로그 생성 관련 서비스
    @Transactional
    public BlogResponse addBlog(CustomUserDetails customUserDetails, UpdateBlogRequest request) {
        // 사용자 확인 및 중복된 블로그 확인
        User user = globalService.findUser(customUserDetails);
        blogRepository.findByGitRepoUrl(request.getGitRepoUrl())
                .ifPresent(blog -> { throw new RuntimeException("이미 해당 레포지토리의 블로그가 존재합니다."); });

        // Blog 생성 및 저장
        Blog blog = Blog.builder()
                .user(user)
                .blogName(request.getBlogName())
                .description(request.getDescription())
                .gitRepoUrl(request.getGitRepoUrl())
                .favCount(0)
                .build();
        blogRepository.save(blog);

        List<BlogMember> blogMembers = request.getBlogMember().stream()
                .distinct()
                .filter(name -> !name.equals(user.getUserName()))
                .map(name -> new BlogMember(name, blog))
                .collect(Collectors.toList());

        BlogMember userMember = new BlogMember(user.getUserName(), blog);
        blogMembers.add(userMember);
        blogMemberRepository.saveAll(blogMembers);

        Set<String> members = blogMembers.stream()
                .map(BlogMember::getUserName)
                .collect(Collectors.toSet());

        return BlogResponse.createWith(blog, members);
    }

    //블로그 목록 불러오기
    public List<BlogEventResponse> getAllBlogEvents(CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);
        List<Blog> blogList = blogMemberRepository.findBlogMemberByUserName(user.getUserName())
                .stream().map(user1 -> user1.getBlog()).collect(Collectors.toList());

        return blogList.stream()
                .map(BlogEventResponse::new)
                .collect(Collectors.toList());
    }

    // 특정 블로그 검색 관련 서비스

    public GetBlogResponse getBlog(Long blogId) {
        Blog blog = globalService.findBlogById(blogId);

        Set<String> members = blogMemberRepository.findBlogMemberByBlogId(blogId)
                .stream().map(member -> member.getUserName()).collect(Collectors.toSet());

        List<BreakThrough> breakThroughs = getArticles(blogId);

        return GetBlogResponse.createWith(blog,members,breakThroughs,false);
    }

    @Transactional
    // 블로그 수정 관련 서비스
    public GetBlogResponse updateBlog(CustomUserDetails customUserDetails, Long blogId, UpdateBlogRequest request){
        User user = globalService.findUser(customUserDetails);
        Blog blog = globalService.findBlogById(blogId);

        if(blog.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        blog.updateBlog(UpdateBlogData.createWith(request));
        blogRepository.save(blog);

        request.getBlogMember().stream()
                .filter(member -> !blogMemberRepository.findBlogMemberByUserNameAndBlog(member, blog).isPresent()) // 이미 존재하지 않으면
                .map(member -> {
                    BlogMember newBlogMember = new BlogMember(member, blog);
                    return blogMemberRepository.save(newBlogMember); // 새로 생성한 BlogMember를 저장
                })
                .collect(Collectors.toList());

        List<BlogMember> existingMembers = blogMemberRepository.findBlogMemberByBlog(blog);
        existingMembers.stream()
                .filter(existingMember -> !request.getBlogMember().contains(existingMember.getUserName())) // request에 없는 멤버
                .forEach(existingsMember -> blogMemberRepository.delete(existingsMember));

        Boolean isFavorited = favoritesRepository.findByUserIdAndBlogId(customUserDetails.getId(), blogId)
                .map(Favorites::getIsFavorited)
                .orElse(false);

        Set<String> members = blogMemberRepository.findBlogMemberByBlogId(blogId)
                .stream().map(member -> member.getUserName()).collect(Collectors.toSet());

        List<BreakThrough> breakThroughs = getArticles(blogId);

        return GetBlogResponse.createWith(blog,members,breakThroughs, isFavorited);
    }
    @Transactional
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

        Set<String> members = blogMemberRepository.findBlogMemberByBlogId(blogId)
                .stream().map(member -> member.getUserName()).collect(Collectors.toSet());

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

    //즐겨찾기 상위 10개 블로그 반환
    public List<BlogListResponse> getTopFavBlogs() {
        return blogRepository.findTop10ByOrderByFavCountDesc().stream()
                .map(blog -> new BlogListResponse(blog))
                .collect(Collectors.toList());
    }

    //유저가 즐겨찾기한 블로그 모음
    public List<BlogListResponse> getFavBlogs(CustomUserDetails customUserDetails) {
        User user = globalService.findUser(customUserDetails);

        return favoritesRepository.findByUserIdAndIsFavoritedTrue(user.getId()).stream()
                .map(favorites -> BlogListResponse.createWithBlogList(favorites.getBlog()))
                .collect(Collectors.toList());
    }
}
