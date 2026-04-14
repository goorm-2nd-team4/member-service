package com.goorm.membership.Controller;

import com.goorm.membership.domain.Member;
import com.goorm.membership.repository.MemberRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*") // 프론트와 연동을 위한 설정
public class MemberApiController {

    private final MemberRepository memberRepository;

    public MemberApiController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 조회
    @GetMapping
    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    // 추가
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberRepository.deleteById(id);
    }

    // 수정
    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody Member updateData) {
        Member member = memberRepository.findById(id).orElseThrow();
        member.setName(updateData.getName());
        member.setAge(updateData.getAge());
        return memberRepository.save(member);
    }
}