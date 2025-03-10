package likelion.devbreak.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion.devbreak.domain.dto.request.UpdateBlogRequest;
import likelion.devbreak.domain.dto.response.BlogEventResponse;
import likelion.devbreak.domain.dto.response.BlogListResponse;
import likelion.devbreak.domain.dto.response.BlogResponse;
import likelion.devbreak.domain.dto.response.GetBlogResponse;
import likelion.devbreak.dto.ResponseDto;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Blog 관련 API")
public class BlogController {

    private final BlogService blogService;

    // 블로그 생성
    @PostMapping("/blog")
    @Operation(summary = "블로그 생성 API")
    public ResponseEntity<ResponseDto> addBlog(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UpdateBlogRequest request) {
        log.info("Request to POST Blog");
        BlogResponse response = blogService.addBlog(customUserDetails, request);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // 사용자의 모든 블로그 조회
    @GetMapping("/blog")
    @Operation(summary = "개인 블로그 목록 조회 API")
    public ResponseEntity<?> findAllBlogs(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        log.info("Request to GET all blogs");
        try {
            List<BlogEventResponse> response = blogService.getAllBlogEvents(customUserDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 특정 블로그 조회
    @GetMapping("/blog/non-member/{blogId}")
    @Operation(summary = "특정 블로그 조회 API", description = "토큰 필요 X")
    public ResponseEntity<ResponseDto> getBlog(
            @PathVariable("blogId") Long blogId) {
        log.info("Request to GET a Blog");
        GetBlogResponse response = blogService.getBlog(blogId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // 특정 블로그 수정
    @PutMapping("/blog/{blogId}")
    @Operation(summary = "특정 블로그 수정 API")
    public ResponseEntity<ResponseDto> updateBlog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("blogId") Long blogId,
            @RequestBody UpdateBlogRequest request) {
        GetBlogResponse response = blogService.updateBlog(customUserDetails, blogId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //블로그 즐겨찾기 기능
    @PutMapping("/blog/{blogId}/favorites")
    @Operation(summary = "블로그 즐겨찾기(토글 형식) API")
    public ResponseEntity<?> favToggle(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("blogId") Long blogId){
        try {
            GetBlogResponse response = blogService.favoriteToggle(customUserDetails,blogId);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
        }
    }

    @DeleteMapping("/blog/{blogId}")
    @Operation(summary = "특정 블로그 삭제 API")
    public ResponseEntity<?> deleteBlog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("blogId") Long blogId){
        try {
            blogService.deleteBlog(customUserDetails, blogId);
            return ResponseEntity.ok().body("블로그가 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //즐겨찾기 상위 10개 블로그 반환
    @GetMapping("/home/blog")
    @Operation(summary = "즐겨찾기 상위 10개 블로그 목록 조회 API", description = "토큰 필요 X")
    public ResponseEntity<?> getTopFavBlogs() {
        try {
            List<BlogListResponse> response = blogService.getTopFavBlogs();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 유저가 즐겨찾기한 블로그 모음
    @GetMapping("/home/blog/like")
    @Operation(summary = "유저가 즐겨찾기한 블로그 목록 조회 API")
    public ResponseEntity<?> getFavBlog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        log.info("Get All FavBlogs");
        try {
            List<BlogListResponse> response = blogService.getFavBlogs(customUserDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
