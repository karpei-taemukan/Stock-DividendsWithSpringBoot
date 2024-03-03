package com.zerobase.model;

import com.zerobase.persist.entity.MemberEntity;
import lombok.*;

import java.util.List;

public class Auth {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignIn{
        private String username;
        private String password;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignUp{
        private String username;
        private String password;
        private List<String> roles; // 사용자 권한

        public MemberEntity toEntity(){
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }

}
