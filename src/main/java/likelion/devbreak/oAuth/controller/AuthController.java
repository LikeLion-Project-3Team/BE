package likelion.devbreak.oAuth.controller;

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
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final OAuthLoginService oAuthLoginService;

    // GET 방식으로 code와 state 받기
    @GetMapping("/github")
    public ResponseEntity<?> githubCallback(@RequestParam(name = "code") String code) {
        try {
            LoginParams params = new LoginParams(code);
            // code와 state를 전달하여 토큰 발급
            return ResponseEntity.ok(oAuthLoginService.login(params));
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
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        oAuthLoginService.logout(customUserDetails.getId());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthTokens tokens = oAuthLoginService.refresh(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        oAuthLoginService.delete(customUserDetails);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal CustomUserDetails user) {
        NameResponse info = oAuthLoginService.getInfo(user.getId());
        return ResponseEntity.ok(info);
    }
}