package com.example.todoai.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인 상태 점검용 컨트롤러
 * - /me/jwt    : 자체 로그인(JWT) 경로. JwtAuthFilter가 SecurityContext에 심은 인증을 읽는다.
 * - /me/oauth2 : 소셜 로그인(OAuth2) 경로. OAuth2User의 attributes를 그대로 노출한다.
 * - /          : 헬스 체크 겸 공개 엔드포인트.
 */
@RestController
public class MeCotroller {
	
	 /** 
     * JWT 기반 로그인 확인
     * - Authorization: Bearer <accessToken> 헤더 필수
     * - JwtAuthFilter가 UsernamePasswordAuthenticationToken(principal = username, authorities) 를 넣어둔다.
     */
	@GetMapping("me/jwt")
	public Map<String, Object> meJwt(Authentication auth){
		String username = (String) auth.getPrincipal();		//JwtAuthFilter에서 principal로 username 저장
		var roles = auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)		//예: "ROLE_USER"
				.collect(Collectors.toList());
		return Map.of("provider", "jwt", "username", username, "roles", roles);
	}
	
	 /**
     * OAuth2(구글 등) 로그인 확인
     * - Spring Security가 넣어주는 OAuth2User에서 프로필 attributes 꺼냄.
     * - 로그인 성공 후 세션 기반으로 동작(너는 동시에 JWT도 쓰므로 둘 다 공존).
     */
	@GetMapping("/me/oauth2")
	public Map<String, Object> meOauth2(
		@AuthenticationPrincipal OAuth2User oauth2User) {
		Map<String, Object> attrs = oauth2User.getAttributes();		//구글 프로필: sub, email, name
		return Map.of("provider", "oauth2", "attributes", attrs);
	}
	@GetMapping("/")
	public String home() {
		return "ok";
	}
}
