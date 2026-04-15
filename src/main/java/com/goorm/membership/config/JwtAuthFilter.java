package com.goorm.membership.config;

import com.goorm.membership.service.MemberDetailsService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final MemberDetailsService memberDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, java.io.IOException {
        
        // Authorization 헤더 추출
        String authHeader = request.getHeader("Authorization");

        // Bearer 로 시작하는 경우에만 토큰 처리
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 토큰 값만 추출

            if(jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);

                // 이미 인증된 요청이 아닌 경우에만 처리
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 이메일로 사용자 조회
                    UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

                    // 인증 객체 생성 후 SecurityContext 에 등록
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
