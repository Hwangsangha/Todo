package com.example.todoai.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
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
		//Authorization 헤더에서 Bearer 토큰 추출
		String authz = request.getHeader("Authorization");
		String token = null;
		if(authz != null && authz.startsWith("Bearer")) {
			token = authz.substring(7).trim();
		}
		
		if(token == null || token.isEmpty()) {
			//토큰이 아예 없으면 필터가 인증을 않하고 건너뜀.
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			//토큰 검증 + 클레임 파싱 (서명/만료 확인)
			Jws<Claims> jws = jwt.parse(token);
			Claims claims = jws.getPayload();
			
			//username(sub)와 권한(roles)꺼내기
			String username = claims.getSubject();
			@SuppressWarnings("unchecked")
			List<String> roles = (List<String>) claims.getOrDefault("roles", List.of());
			
			//"USER" 같은 값들을 Spring Security 권한으로 변환("ROLE_USER")
			Collection<SimpleGrantedAuthority> authorities = roles.stream()
					.map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
			
			//인증 객체 생성(비밀번호는 필요 없으니 null)
			var authentication = 
					new UsernamePasswordAuthenticationToken(username, null, authorities);
			
			//이 요청 스레드의 SecurityContext에 인증 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			//다음 필터로 진행
			filterChain.doFilter(request, response);
		}catch(JwtException e) {
			//토큰이 있었는데 유효하지 않으면 401로 응답하고 종료
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"error\":\"invalid_or_expired_token\"}");
		}
	}
}
