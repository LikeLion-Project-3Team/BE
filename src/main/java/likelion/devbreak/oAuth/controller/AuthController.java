package likelion.devbreak.oAuth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import likelion.devbreak.domain.User;
import likelion.devbreak.oAuth.domain.AuthTokens;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.oAuth.domain.RefreshTokenRequest;
import likelion.devbreak.oAuth.domain.dto.response.NameResponse;
import likelion.devbreak.oAuth.domain.github.LoginParams;
import likelion.devbreak.oAuth.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "유저 관련 API")
public class AuthController {

    private final OAuthLoginService oAuthLoginService;

    // GET 방식으로 code와 state 받기
    @GetMapping("/github")
    @Operation(summary = "로그인 API", description = "사용 X")
    public ResponseEntity<?> githubCallback(@RequestParam(name = "code") String code, HttpServletResponse response) {
        try {
            LoginParams params = new LoginParams(code);
            AuthTokens authTokens = oAuthLoginService.login(params, response);
            log.info("Access Token: {}", authTokens.getAccessToken());
            // code와 state를 전달하여 토큰 발급
            return ResponseEntity.ok(authTokens);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        oAuthLoginService.logout(customUserDetails.getId());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    @Operation(summary = "새로운 Access Token 발급 API")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthTokens tokens = oAuthLoginService.refresh(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }

    @DeleteMapping("/delete-account")
    @Operation(summary = "계정 삭제 API")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        oAuthLoginService.delete(customUserDetails);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @GetMapping("/info")
    @Operation(summary = "유저의 깃허브 정보 반환 API")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal CustomUserDetails user) {
        NameResponse info = oAuthLoginService.getInfo(user.getId());
        return ResponseEntity.ok(info);
    }
}