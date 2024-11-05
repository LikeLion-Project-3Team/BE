package likelion.devbreak.service;

import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.response.AddBlogResponse;
import likelion.devbreak.domain.dto.response.BlogEventResponse;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import likelion.devbreak.repository.BlogRepository;
import likelion.devbreak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public AddBlogResponse addBlog(Long userId, UpdateBlogRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        Blog blog = Blog.builder()
                .blogName(request.getBlogName())
                .description(request.getDescription())
                .gitRepoUrl(request.getGitRepoUrl())
                .build();
        blogRepository.save(blog);
        return AddBlogResponse.createWith(blog);
    }

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
}
