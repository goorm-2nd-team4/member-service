package com.goorm.membership.api.member;

import com.goorm.membership.entity.Member;
import com.goorm.membership.dto.common.ApiResponse;
import com.goorm.membership.dto.member.request.LoginRequest;
import com.goorm.membership.dto.member.request.RegisterRequest;
import com.goorm.membership.dto.member.response.LoginResponse;
import com.goorm.membership.dto.member.response.MemberDetailResponse;
import com.goorm.membership.dto.member.response.MemberSummaryResponse;
import com.goorm.membership.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> register(@Valid @RequestBody RegisterRequest request) {
        Member member = memberService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공", MemberDetailResponse.from(member)));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Member member = memberService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", LoginResponse.from(member)));
    }

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<MemberSummaryResponse>>> getMembers() {
        List<MemberSummaryResponse> members = memberService.findAll().stream()
                .map(MemberSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("회원 목록 조회 성공", members));
    }

//    // 상세 조회 기능 (보류)
//    @GetMapping("/members/{id}")
//    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMember(@PathVariable Long id) {
//        Member member = memberService.findById(id);
//        return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 성공", MemberDetailResponse.from(member)));
//    }
}
