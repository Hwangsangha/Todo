package com.example.todoai.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity	//JPA 엔티티 클래스임을 
@Table(name = "users")	// 테이블 이름 지정
@Getter @Setter
@NoArgsConstructor	//기본 생성자 자동생성
@AllArgsConstructor	//모든 필드 포함 생성자 자동생성
@Builder	//빌더패턴 자동생성
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	// auto_increment
	private Long id;
	
	@Column(nullable = false, unique = true)	//null 불가, 중복불가
	private String username;
	
	@Column(nullable = false)
	private String password;	//나중에 BCrypt 등으로 암호화 저장 예정
	
	@Column(nullable = false, unique = true)
	private String email;
	
	private LocalDateTime createdAt;	//생성 시각
	private LocalDateTime updatedAt;	//수정 시각
}
