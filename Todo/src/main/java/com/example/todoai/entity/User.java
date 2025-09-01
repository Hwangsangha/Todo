package com.example.todoai.entity;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity	//JPA 엔티티 클래스임을 
@Table(name = "users",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_users_username", columnNames = "username"),
				@UniqueConstraint(name = "uk_users_email", columnNames = "email")
		})	// 테이블 이름 지정
@Getter @Setter
@NoArgsConstructor	//기본 생성자 자동생성
@AllArgsConstructor	//모든 필드 포함 생성자 자동생성
@Builder	//빌더패턴 자동생성
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	// auto_increment
	private Long id;
	
	@Column(nullable = false, length = 30)	//null 불가, 중복불가
	private String username;
	
	@Column(nullable = false, length = 225)
	private String password;	//나중에 BCrypt 등으로 암호화 저장 예정
	
	@Column(nullable = false, length = 120)
	private String email;
	
	@Enumerated(EnumType.STRING)			//Enum을 문자열로 저장
	@Column(nullable = false, length = 20)
	@Builder.Default						//빌더 사용 시에도 기본값 유지
	private Role role = Role.ROLE_USER;		//기본 권한은 USER
	
	@Column(nullable = false)
	private LocalDateTime createdAt;	//생성 시각
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;	//수정 시각
	
	@PrePersist	//insert 실행 전 자동 호출
	protected void onCreate() {
		var now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}
	
	@PreUpdate	//update 실행 전 자동 호출
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
	@Column(length = 512)
	private String refreshToken;
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
