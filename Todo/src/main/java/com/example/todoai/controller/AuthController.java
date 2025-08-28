package com.example.todoai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todoai.entity.Role;
import com.example.todoai.entity.User;
import com.example.todoai.repository.UserRepository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")	//인증 관련 엔드포인트 prefix
@Validated					//DTO의 @NotBlank, @Email 검증 활성화
public class AuthController {
	
	private final UserRepository users;
	private final PasswordEncoder encoder;
	
	public AuthController(UserRepository users, PasswordEncoder encoder) {
		this.users = users;
		this.encoder = encoder;
	}
	
	public static record SignupRequest(
			@NotBlank String username,		//공백/빈문자 금지
			@Email @NotBlank String email,	//이메일 형식, 공백 금지
			@NotBlank String password		//공백 금지
			) {}
	
	/**
     * 회원가입
     * - username/email 중복 체크
     * - 비밀번호는 BCrypt 해시로 저장
     * - 성공 시 200 OK, 실패 시 409 Conflict
     */
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody SignupRequest req){
		//중복체크 (DB인덱스도 있으니 이중 안전장치)
		if(users.existsByUsername(req.username())) {
			return ResponseEntity.status(409).body(Map.of("error", "username_in_use"));
		}
		if(users.existsByEmail(req.email())) {
			return ResponseEntity.status(409).body(Map.of("error", "email_in_use"));
		}
		
		//비밀번호 해시 (평문저장금지)
		String hashed = encoder.encode(req.password);
		
		User u = User.builder()
				.username(req.username())
				.email(req.email())
				.password(hashed)
				.role(Role.ROLE_USER)
				.build();
		users.save(u);
		
		//응답(필요하면 새 ㅇ저의 id정도만 내려도 됨)
		return ResponseEntity.ok(Map.of("result", "ok"));
	}
}
