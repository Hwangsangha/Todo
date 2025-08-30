package com.example.todoai.controller;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todoai.dto.LoginRequest;
import com.example.todoai.dto.SignupRequest;
import com.example.todoai.entity.Role;
import com.example.todoai.entity.User;
import com.example.todoai.repository.UserRepository;
import com.example.todoai.security.JwtService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")	//인증 관련 엔드포인트 prefix
@Validated					//DTO의 @NotBlank, @Email 검증 활성화
public class AuthController {
	
	private final UserRepository users;
	private final PasswordEncoder encoder;
	private final JwtService jwt;
	
	public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
		this.users = users;
		this.encoder = encoder;
		this.jwt = jwt;
	}
	
	/**
     * 회원가입
     * - username/email 중복 체크
     * - 비밀번호는 BCrypt 해시로 저장
     * - 성공 시 200 OK, 실패 시 409 Conflict
     */
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody @Validated SignupRequest req){
		//중복체크 (DB인덱스도 있으니 이중 안전장치)
		if(users.existsByUsername(req.userename())) {
			return ResponseEntity.status(409).body(Map.of("error", "username_in_use"));
		}
		if(users.existsByEmail(req.email())) {
			return ResponseEntity.status(409).body(Map.of("error", "email_in_use"));
		}
		
		//비밀번호 해시 (평문저장금지)
		String hashed = encoder.encode(req.password());
		
		User u = User.builder()
				.username(req.userename())
				.email(req.email())
				.password(hashed)
				.role(Role.ROLE_USER)
				.build();
		User saved = users.save(u);
		
		//응답(필요하면 새 ㅇ저의 id정도만 내려도 됨)
		return ResponseEntity
				.created(URI.create("/users/" + saved.getId()))
				.body(Map.of("result", "ok", "id", saved.getId()));
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Validated LoginRequest req){
		//사용자 조회
		Optional<User> opt = users.findByUsername(req.username());
		if(opt.isEmpty()) {
			//계정 존재 유무를 노출하지 않기 위해 메시지는 통일
			return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
		}
		User user = opt.get();
		
		//비밀번호 검증 틀리면 401
		if(!encoder.matches(req.password(), user.getPassword())) {
			return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
		}
		
		//권한을 문자열 리스트로 준비(예: ROLE_USER > USER)
		List<String> roles = List.of(user.getRole().name().replace("ROLE_", ""));
		
		//jwt 발급
		String accessToken = jwt.issueAccess(user.getUsername(), roles);
		String refreshToken = jwt.issueRefresh(user.getUsername());
		
		//Refresh 토큰은 HttpOnly 쿠키로 내려주는 것을 권장
		ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
				.httpOnly(true)					//JS에서 접근 불가
				.secure(false)					//개발 중 http 사용이면 false, 배포는 true(Https)
				.sameSite("Lax")				//기본 보호. SPA 라우팅에 보통 문제 없음
				.path("/")						//전체 경로에 쿠키 전송
				.maxAge(Duration.ofDays(7))		//JwtService설정과 일치시키는게 깔끔
				.build();
				
		//응답: Body엔 accessToken, 헤더엔 Set-Cookie로 refresh 토큰
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body(Map.of(
						"accessToken", accessToken,
						"tokenType", "Bearer",
						"expiresIn", jwt.remainingSeconds(accessToken)));
	}
}
