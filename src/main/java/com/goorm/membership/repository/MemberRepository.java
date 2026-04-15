package com.goorm.membership.repository;

import com.goorm.membership.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
//    이메일로 로그인 인증 시 사용
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
