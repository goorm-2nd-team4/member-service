package com.goorm.membership.api.member;

import com.goorm.membership.config.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public MemberApiController(MemberService memberService, JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    /* =========================
       AUTH (로그인 / 회원가입)
    ========================= */

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        Member member = memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공", MemberDetailResponse.from(member)));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        Member member = memberService.login(request);
        String token = jwtUtil.generateToken(member.getEmail());

        return ResponseEntity.ok(
                ApiResponse.success("로그인 성공", LoginResponse.from(member, token))
        );
    }

    /* =========================
       MEMBER CRUD
    ========================= */

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<MemberSummaryResponse>>> getMembers() {

        List<MemberSummaryResponse> members = memberService.findAll()
                .stream()
                .map(MemberSummaryResponse::from)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("회원 목록 조회 성공", members)
        );
    }

    @PostMapping("/members")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> createMember(
            @RequestBody RegisterRequest request) {

        Member member = memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원 생성 성공", MemberDetailResponse.from(member)));
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> updateMember(
            @PathVariable Long id,
            @RequestBody RegisterRequest request) {

        Member member = memberService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("회원 수정 성공", MemberDetailResponse.from(member))
        );
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable Long id) {

        memberService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success("회원 삭제 성공", null)
        );
    }

    /* =========================
       (보류) 상세조회
    ========================= */
    // @GetMapping("/members/{id}")
    // public ResponseEntity<ApiResponse<MemberDetailResponse>> getMember(@PathVariable Long id) {
    //     Member member = memberService.findById(id);
    //     return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 성공",
    //             MemberDetailResponse.from(member)));
    // }
}