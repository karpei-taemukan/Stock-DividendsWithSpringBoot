package com.zerobase.persist;

import com.zerobase.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRespository extends JpaRepository<MemberEntity,Long>{
    Optional<MemberEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
