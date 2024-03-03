package com.zerobase.web;

import com.zerobase.model.Auth;
import com.zerobase.persist.entity.MemberEntity;
import com.zerobase.security.TokenProvider;
import com.zerobase.service.MemberService;
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



    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody Auth.SignUp req
    ){
        // 회원 가입 API
        MemberEntity register = this.memberService.register(req);

        return ResponseEntity.ok(register);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(
            @RequestBody Auth.SignIn req
    ){
        // id password 검증

        MemberEntity authenticateUser = this.memberService.authenticate(req);

        // token 제공
        String token = this.tokenProvider
                .generateToken(authenticateUser.getUsername(), authenticateUser.getRoles());

        log.info("user login -> "+ req.getUsername());

        return ResponseEntity.ok(token);
    }
}
