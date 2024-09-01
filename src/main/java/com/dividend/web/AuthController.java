package com.dividend.web;

import com.dividend.model.Auth;
import com.dividend.persist.entity.MemberEntity;
import com.dividend.security.TokenProvider;
import com.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/sign-up")
    public ResponseEntity<MemberEntity> signUp(@RequestBody Auth.SignUp request) {
        MemberEntity result = memberService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody Auth.SignIn request) {
        MemberEntity member = memberService.authenticate(request);
        String token = tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("사용자 로그인 -> " + request.getUsername());
        return ResponseEntity.ok(token);
    }
}
