package com.zerobase.security;

import com.zerobase.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;


/**
 *  토큰 생성(발급)
 *  */

@Component
@RequiredArgsConstructor
public class TokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour
    private final MemberService memberService;

    public String generateToken(String username, List<String> roles) {
        Claims claims   // 사용자의 권한 정보 저장
                = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles); // 키 값 형태로 저장

        var now = new Date();
        var expireDAte = new Date(now.getTime() * TOKEN_EXPIRE_TIME);
        return  Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expireDAte) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘 비밀키
                .compact();
    }

    public String getUsername(String token){
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token){
        if(!StringUtils.hasText(token)){ // token 이 빈값일 경우
            return false;
        }

        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
        // token 의 만료 시간이 현재 시간보다 이전인지 아닌지 만료여부 체크
    }

    private Claims parseClaims(String token){
        try {
           return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }



    //############################################################################################

    // JWT 토큰으로부터 인증 정보를 가져오는 메소드
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails
                = this.memberService.loadUserByUsername(this.getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );

    }


}
