package likelion.devbreak.controller;

import likelion.devbreak.domain.dto.response.BlogResponse;
import likelion.devbreak.domain.dto.response.BlogEventResponse;
import likelion.devbreak.domain.dto.response.GetBlogResponse;
import likelion.devbreak.dto.ResponseDto;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ResponseDto> addBlog(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UpdateBlogRequest request) {
        log.info("Request to POST Blog");
        BlogResponse response = blogService.addBlog(customUserDetails, request);
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

    // 특정 블로그 조회
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<ResponseDto> getBlog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("blogId") Long blogId) {
        log.info("Request to GET a Blog");
        GetBlogResponse response = blogService.getBlog(blogId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 특정 블로그 수정
    @PutMapping("blog/{blogId}")
    public ResponseEntity<ResponseDto> updateBlog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("blogId") Long blogId,
            @RequestBody UpdateBlogRequest request) {
        BlogResponse response = blogService.updateBlog(blogId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
