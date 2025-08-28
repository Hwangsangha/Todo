package com.example.todoai.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
		return Jwts.builder()
				.subject(username)
				.claim("roles", roles)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(accessExpSeconds)))
				.signWith(key, Jwts.SIG.HS256)
				.compact();
	}
}
