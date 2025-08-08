package com.example.todoai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todoai.entity.User;
import com.example.todoai.service.UserService;

import lombok.RequiredArgsConstructor;

//사용자(User) 관련 REST API 엔드포인트를 정의하는 컨트롤러 클래스
//MVP단계: entity를 직접 바인딩
@RestController
@RequestMapping("/api/users")	//공통 url
@RequiredArgsConstructor	//생성자 주입 자동생성(final필드 필요)
public class UserController {

	private final UserService userService;//서비스 계층 의존
	
	//사용자 생성
	@PostMapping
	public ResponseEntity<User> create(@RequestBody User user){
		User createduser = userService.creatUser(user);
		//Http 200코드 + 저장된 사용자 정보 반환
		return ResponseEntity.ok(createduser);
	}
	
	
	//사용자 단건 조회
	@GetMapping("/{id}")
	public ResponseEntity<User> get(@PathVariable Long id){
		User foundUser = userService.getUser(id);
		return ResponseEntity.ok(foundUser);//호출, 없으면 404처리
	}
	
	//사용자 수정
	@PutMapping("/{id}")
	public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user){
		User updatedUser = userService.updatedUser(id, user);
		return ResponseEntity.ok(updatedUser);
	}
	
	//사용자 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
