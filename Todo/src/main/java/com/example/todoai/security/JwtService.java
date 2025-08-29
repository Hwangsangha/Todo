package com.example.todoai.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT 발급/검증 전용 유틸.
 * - 발급: subject(username), roles, 만료시간 → HS256 서명
 * - 검증: 서명/만료 확인 후 클레임 반환
 */
@Component
public class JwtService {
	private final SecretKey key;
	private final long accessExpSeconds;  // AccessToken 만료시간(초)
	private final long refreshExpSeconds; // RefreshToken 만료시간(초)
	
	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.access-exp-seconds:900}") long accessExpSeconds,
			@Value("${app.jwt.refresh-exp-seconds:604800}") long refreshExpSeconds
			) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessExpSeconds = accessExpSeconds;
		this.refreshExpSeconds = refreshExpSeconds;
	}
	public String issueAccess(String username, List<String> roles) {
		Instant now = Instant.now();
		return Jwts.builder()													//Jwts: JWT 유틸 패곹리. builder()로 토큰 만들기 시작.
				.subject(username)												//sub: 표준 클레임. 토큰 주체(로그인 아이디)
				.claim("roles", roles)											//커스텀 클레임. 정해놓은 키로 권한 목록을 넣는다.
				.issuedAt(Date.from(now))										//iat: 발급 시각. 언제 만들었는지 기록.
				.expiration(Date.from(now.plusSeconds(accessExpSeconds)))		//exp: 만료 시각. 만료되면 토큰 무효.
				.signWith(key, Jwts.SIG.HS256)									//서명: 비밀키+알고리즘(HS256)으로 서명 붙이기.
				.compact();														//빌더 종료. header.payload.signature문자열로 직렬화해서 반환.
	}
	
	public Jws<Claims> parse(String token){
		return Jwts.parser()				//Jwts: JWT헬퍼. parser() > 토큰 해석기 만들기 시작
				.verifyWith(key)			//key: 토큰을 발급할 때 쓴 비밀키. 서명 검증용.
				.build()					//빌더 완성.
				.parseSignedClaims(token);	//토큰 문자열을 집어넣어 header.payload.signature풀고, 서명 검증 + 만료 검사까지
	}
}
