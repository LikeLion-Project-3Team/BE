package likelion.devbreak.service;

import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.response.BlogResponse;
import likelion.devbreak.domain.dto.response.BlogEventResponse;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import likelion.devbreak.domain.dto.response.GetBlogResponse;
import likelion.devbreak.dto.UpdateBlogData;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.BlogRepository;
import likelion.devbreak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    // 블로그 생성 관련 서비스
    public BlogResponse addBlog(CustomUserDetails customUserDetails, UpdateBlogRequest request) {
        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        Blog blog = Blog.builder()
                .user(user)
                .blogName(request.getBlogName())
                .description(request.getDescription())
                .gitRepoUrl(request.getGitRepoUrl())
                .build();
        blogRepository.save(blog);
        return BlogResponse.createWith(blog);
    }
    
    // 한 유저에 대한 모든 블로그 관련 서비스
    public List<Blog> returncat(Long userId) {
        return blogRepository.findAllByUserId(userId);
    }

    public List<BlogEventResponse> getAllBlogEvents(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return returncat(user.getId())
                .stream()
                .map(blog -> new BlogEventResponse(blog))
                .collect(Collectors.toList());
    }

    // 특정 블로그 검색 관련 서비스
    public Blog findBlogById(final Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(()->new NotFoundException("블로그를 발견하지 못하였습니다."));
    }
    public GetBlogResponse getBlog(Long blogId) {
        Blog blog = findBlogById(blogId);
        return GetBlogResponse.createWith(blog);
    }

    // 블로그 수정 관련 서비스
    public BlogResponse updateBlog(Long blogId, UpdateBlogRequest request){
        Blog blog = findBlogById(blogId);
        UpdateBlogData updateBlogData = UpdateBlogData.createWith(request);
        blog.updateBlog(updateBlogData);
        blogRepository.save(blog);
        return BlogResponse.createWith(blog);
    }
}
