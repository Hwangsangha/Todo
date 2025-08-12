package com.example.todoai.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity	//jpa 엔티티이며 DB테이블로 매핑됨
@Table(name = "tasks") //테이블명 명시
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	//자동증가
	private Long id;
	
	//User와 다대일(N:1) 관계 - 한 명의 사용자가 여러 개의 할 일을 가질 수 있음
	@ManyToOne(fetch = FetchType.EAGER) // 지연 로딩(성능 최적화)
	@JoinColumn(name = "user_id")	//FK설정
	private User user;
	
	@Column(nullable = false)
	private String title;
	
	private String description; // 할일 설명
	
	private LocalDate dueDate;	// 마감 날짜
	
	@Enumerated(EnumType.STRING)	//Enum값을 문자열로 DB에 저장
	private Priority priority;		//LOw, MEDIUM, HIGH 우선순위 타입
	
	@Enumerated(EnumType.STRING)
	private Status status;		//TODO, IN_PROGRESS, DONE 할일, 진행, 마침
	
	private LocalDateTime createdAt;	//생성일시
	private LocalDateTime updatedAt;	//수정일시

	
	
}
