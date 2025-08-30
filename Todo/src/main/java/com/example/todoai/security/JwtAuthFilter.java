package com.example.todoai.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 요청당 한 번 실행되는 JWT 인증 필터.
 * 1) /auth/** 등 공개 엔드포인트는 건너뜀
 * 2) Authorization: Bearer <token> 헤더를 읽음
 * 3) JwtService.parse()로 서명/만료 검증
 * 4) 성공하면 SecurityContext에 인증 객체 세팅
 * 5) 실패하면 401(토큰이 있었는데 잘못된 경우), 없으면 그냥 다음 필터로
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter{
	
	private final JwtService jwt;
	
	public JwtAuthFilter(JwtService jwt) {
		this.jwt = jwt;
	}
	
	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		String p = request.getRequestURI();
		//필요시 공개 경로 추가
		return p.startsWith("/auth/")
				|| p.startsWith("/h2-console/")
				|| "OPTIONS".equalsIgnoreCase(request.getMethod());
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}
}
