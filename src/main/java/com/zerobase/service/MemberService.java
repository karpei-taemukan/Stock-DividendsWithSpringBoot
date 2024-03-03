package com.zerobase.service;

import com.zerobase.exception.implement.AlreadyExistsUserException;
import com.zerobase.model.Auth;
import com.zerobase.persist.entity.MemberEntity;
import com.zerobase.persist.MemberRespository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRespository memberRespository;

    // password 를 한번 encode 해서 DB에 저장
    private final PasswordEncoder passwordEncoder; // --> 어떤 bean 을 쓸지 정의해야함(AppConfig)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //spring security 에서 제공되는 기능을 사용하기 위함

        return this.memberRespository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Coudn't find user -> " + username));
    }


    public MemberEntity register(Auth.SignUp member){
        boolean exists = this.memberRespository.existsByUsername(member.getUsername());
        if(exists){
            throw new AlreadyExistsUserException();
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));

        return this.memberRespository.save(member.toEntity());
    }



    public MemberEntity authenticate(Auth.SignIn signIn){ // 로그인 검증

        MemberEntity user = this.memberRespository.findByUsername(signIn.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다"));

        // DB 에 저장되있는 PW는 인코딩되있는 반면, 입력한 PW는 인코딩이 안됨
        if(!this.passwordEncoder.matches(signIn.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        return user;
    }
}
