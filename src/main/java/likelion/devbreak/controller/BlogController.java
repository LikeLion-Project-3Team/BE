package likelion.devbreak.controller;

import likelion.devbreak.dto.AddBlogResponse;
import likelion.devbreak.dto.BlogEventResponse;
import likelion.devbreak.dto.ResponseDto;
import likelion.devbreak.dto.UpdateBlogRequest;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlogController {

    private final BlogService blogService;

    // 블로그 생성
    @PostMapping("/blog")
    public ResponseEntity<ResponseDto> addBlog(Authentication authentication, @RequestBody UpdateBlogRequest request) {
        log.info("Request to POST Blog");
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        AddBlogResponse response = blogService.addBlog(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자의 모든 블로그 조회
    @GetMapping("/blog")
    public ResponseEntity<?> findAllBlogs(Authentication authentication) {
        log.info("Request to GET all blogs");
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            List<BlogEventResponse> blogs = blogService.getAllBlogEvents(userId);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
